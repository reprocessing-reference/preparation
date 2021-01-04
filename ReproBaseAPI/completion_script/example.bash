mkdir reports
#S2
python verify_completion.py -o reports/reportS2ECMWFD.txt -d 20150928T100000 -e 20200910T110000 -s 3 -t 'ECMWFD' -m 'S2' &
python verify_completion.py -o reports/reportS2UT1UTC.txt -d 20150619T100000 -e 20191018T110000 -s 168 -t 'AUX_UT1UTC' -m 'S2' &
#not yet available
#python verify_completion.py -o reports/reportS2ARESORB.txt -d 20140101T010000 -e 20191018T110000 -s 24 -t 'AUX_RESORB' -m 'S2A'
#python verify_completion.py -o reports/reportS2ARESORB.txt -d 20140101T010000 -e 20191018T110000 -s 24 -t 'AUX_RESORB' -m 'S2B'
#S1
python verify_completion.py -o reports/reportS1WND.txt -d 20140101T010000 -e 20191018T110000 -s 3 -t 'AUX_WND' -m 'S1' &
python verify_completion.py -o reports/reportS1WAV.txt -d 20140101T010000 -e 20191018T110000 -s 3 -t 'AUX_WAV' -m 'S1' &
python verify_completion.py -o reports/reportS1ICE.txt -d 20140101T010000 -e 20191018T110000 -s 3 -t 'AUX_ICE' -m 'S1' &
python verify_completion.py -o reports/reportS1APOEORB.txt -d 20140407T000000 -e 20200916T000000 -s 1 -t 'AUX_POEORB' -m 'S1A' &
python verify_completion.py -o reports/reportS1BPOEORB.txt -d 20160324T000000 -e 20200916T000000 -s 1 -t 'AUX_POEORB' -m 'S1B' &

wait
echo "Done!"
