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
python verify_completion.py -o reports/reportS1ICE.txt -d 20140101T010000 -e 20191018T110000 -s 24 -t 'AUX_ICE' -m 'S1' &
python verify_completion.py -o reports/reportS1APOEORB.txt -d 20140407T000000 -e 20200916T000000 -s 1 -t 'AUX_POEORB' -m 'S1A' &
python verify_completion.py -o reports/reportS1BPOEORB.txt -d 20160324T000000 -e 20200916T000000 -s 1 -t 'AUX_POEORB' -m 'S1B' &

#S3
python verify_completion.py -o reports/reportS3A_AX___FRO_AX.txt -d 20140101T010000 -e 20191018T110000 -s 24 -t 'S3A_AX___FRO_AX' -m 'S3A' &
python verify_completion.py -o reports/reportS3B_AX___FRO_AX.txt -d 20140101T010000 -e 20191018T110000 -s 24 -t 'S3B_AX___FRO_AX' -m 'S3B' &
python verify_completion.py -o reports/reportS3__AX___MFA_AX.txt -d 20140101T010000 -e 20191018T110000 -s 6 -t 'S3__AX___MFA_AX' -m 'S3' & 
python verify_completion.py -o reports/reportS3__AX___MA1_AX.txt -d 20140101T010000 -e 20191018T110000 -s 6 -t 'S3__AX___MA1_AX' -m 'S3' &
python verify_completion.py -o reports/reportS3__AX___MA2_AX.txt -d 20140101T010000 -e 20191018T110000 -s 6 -t 'S3__AX___MA2_AX' -m 'S3' &
python verify_completion.py -o reports/reportS3__AX___BB2_AX.txt -d 20140101T010000 -e 20191018T110000 -s 720 -t 'S3__AX___BB2_AX' -m 'S3' &
python verify_completion.py -o reports/reportS3A_SR___POEPAX.txt -d 20140101T010000 -e 20191018T110000 -s 24 -t 'S3A_SR___POEPAX' -m 'S3A' &
python verify_completion.py -o reports/reportS3B_SR___POEPAX.txt -d 20140101T010000 -e 20191018T110000 -s 24 -t 'S3B_SR___POEPAX' -m 'S3B' &
python verify_completion.py -o reports/reportS3A_SR_1_USO_AX.txt -d 20140101T010000 -e 20191018T110000 -s 24 -t 'S3A_SR_1_USO_AX' -m 'S3A' &
python verify_completion.py -o reports/reportS3B_SR_1_USO_AX.txt -d 20140101T010000 -e 20191018T110000 -s 24 -t 'S3B_SR_1_USO_AX' -m 'S3B' &
python verify_completion.py -o reports/reportS3__SR_2_RMO_AX.txt -d 20140101T010000 -e 20191018T110000 -s 6 -t 'S3__SR_2_RMO_AX' -m 'S3' &
python verify_completion.py -o reports/reportS3__SR_2_POL_AX.txt -d 20140101T010000 -e 20191018T110000 -s 168 -t 'S3__SR_2_POL_AX' -m 'S3' &
python verify_completion.py -o reports/reportS3A_SR_2_RGI_AX.txt -d 20140101T010000 -e 20191018T110000 -s 24 -t 'S3A_SR_2_RGI_AX' -m 'S3A' &
python verify_completion.py -o reports/reportS3B_SR_2_RGI_AX.txt -d 20140101T010000 -e 20191018T110000 -s 24 -t 'S3B_SR_2_RGI_AX' -m 'S3B' &
python verify_completion.py -o reports/reportS3__SR_2_SIC_AX.txt -d 20140101T010000 -e 20191018T110000 -s 72 -t 'S3__SR_2_SIC_AX' -m 'S3' &
python verify_completion.py -o reports/reportS3__SL_2_SSTAAX.txt -d 20140101T010000 -e 20191018T110000 -s 24 -t 'S3__SL_2_SSTAAX' -m 'S3' &
python verify_completion.py -o reports/reportS3A_SR_2_PCPPAX.txt -d 20140101T010000 -e 20191018T110000 -s 24 -t 'S3A_SR_2_PCPPAX' -m 'S3A' &
python verify_completion.py -o reports/reportS3B_SR_2_PCPPAX.txt -d 20140101T010000 -e 20191018T110000 -s 24 -t 'S3B_SR_2_PCPPAX' -m 'S3B' &



wait
echo "Done!"
