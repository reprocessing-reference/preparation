import cdsapi
import datetime, threading, time, os

def downloadCamsreGribForSmallPeriod(start, stop, outputFilePathName):
  c = cdsapi.Client()
  
  c.retrieve(
      'cams-global-reanalysis-eac4',
      {
          'format': 'grib',
          'variable': [
              'black_carbon_aerosol_optical_depth_550nm', 'dust_aerosol_optical_depth_550nm', 'organic_matter_aerosol_optical_depth_550nm',
              'sea_salt_aerosol_optical_depth_550nm', 'sulphate_aerosol_optical_depth_550nm', 'surface_geopotential',
              'total_aerosol_optical_depth_1240nm', 'total_aerosol_optical_depth_469nm', 'total_aerosol_optical_depth_550nm',
              'total_aerosol_optical_depth_670nm', 'total_aerosol_optical_depth_865nm',
          ],
          'date': start+'/'+stop,
          'time': [
              '00:00', '03:00', '06:00',
              '09:00', '12:00', '15:00',
              '18:00', '21:00',
          ],
      },
      outputFilePathName)

def downloadCamsreGribForLargePeriodInParallel(startDate, stopDate, outputFileDir, rawGribNamePattern):
    #
    # Preparing the launch of parallel downloads by dividing the whole period
    # into smaller ones in order to launch smaller requests
    #
    daysBetweenStartAndStop = (stopDate - startDate).days
    periods = {}
    periodDivider = 30
    for period in range(int(daysBetweenStartAndStop / periodDivider)):
        periodStart = startDate + datetime.timedelta(days = period * periodDivider)
        periodStop = periodStart + datetime.timedelta(days = periodDivider - 1)
        periods[periodStart] = periodStop

    lastPeriodStart = startDate + datetime.timedelta(days = len(periods) * periodDivider)
    lastPeriodStop = stopDate
    periods[lastPeriodStart] = lastPeriodStop

    nbMaxThreads = 6
    generatedGribId = 0
    threadsToWait = []

    #
    # Launching downloads in parallel
    #
    for (periodStart, periodStop) in periods.items():
        generatedGribId += 1
        gribName = rawGribNamePattern % str(generatedGribId)
        outputFileNamePath = os.path.join(outputFileDir, gribName)
        downloadThread = threading.Thread(target = downloadCamsreGribForSmallPeriod, args = (periodStart, periodStop, outputFileNamePath,))
        threadsToWait.append(downloadThread)
        while threading.active_count() > nbMaxThreads:
            time.sleep(5)
        downloadThread.start()
        
    # Wait for all threads to end before returning
    for thread in threadsToWait:
        thread.join()