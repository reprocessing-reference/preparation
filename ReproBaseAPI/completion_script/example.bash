mkdir reports

#S2
python verify_completion.py -o reports/reportS2ECMWFD.txt -s 3 -t 'ECMWFD' -m 'S2' &
python verify_completion.py -o reports/reportS2UT1UTC.txt -s 168 -t 'AUX_UT1UTC' -m 'S2' &
#not yet available
#python verify_completion.py -o reports/reportS2ARESORB.txt -s 24 -t 'AUX_RESORB' -m 'S2A'
#python verify_completion.py -o reports/reportS2ARESORB.txt -s 24 -t 'AUX_RESORB' -m 'S2B'

#S1
python verify_completion.py -o reports/reportS1WND.txt -s 3 -t 'AUX_WND' -m 'S1' &
python verify_completion.py -o reports/reportS1WAV.txt -s 3 -t 'AUX_WAV' -m 'S1' &
python verify_completion.py -o reports/reportS1ICE.txt -s 24 -t 'AUX_ICE' -m 'S1' &
python verify_completion.py -o reports/reportS1APOEORB.txt -s 24 -t 'AUX_POEORB' -m 'S1A' &
python verify_completion.py -o reports/reportS1BPOEORB.txt -s 24 -t 'AUX_POEORB' -m 'S1B' &

#S3
python verify_completion.py -o reports/reportS3A_AX___FRO_AX.txt -s 24 -t 'AX___FRO_AX' -m 'S3A' &
python verify_completion.py -o reports/reportS3B_AX___FRO_AX.txt -s 24 -t 'AX___FRO_AX' -m 'S3B' &
python verify_completion.py -o reports/reportS3__AX___MFA_AX.txt -s 6 -t 'AX___MFA_AX' -m 'S3' & 
python verify_completion.py -o reports/reportS3__AX___MA1_AX.txt -s 6 -t 'AX___MA1_AX' -m 'S3' &
python verify_completion.py -o reports/reportS3__AX___MA2_AX.txt -s 6 -t 'AX___MA2_AX' -m 'S3' &
python verify_completion.py -o reports/reportS3__AX___BB2_AX.txt -s 720 -t 'AX___BB2_AX' -m 'S3' &
python verify_completion.py -o reports/reportS3A_SR___POEPAX.txt -s 24 -t 'SR___POEPAX' -m 'S3A' &
python verify_completion.py -o reports/reportS3B_SR___POEPAX.txt -s 24 -t 'SR___POEPAX' -m 'S3B' &
python verify_completion.py -o reports/reportS3A_SR_1_USO_AX.txt -s 24 -t 'SR_1_USO_AX' -m 'S3A' &
python verify_completion.py -o reports/reportS3B_SR_1_USO_AX.txt -s 24 -t 'SR_1_USO_AX' -m 'S3B' &
python verify_completion.py -o reports/reportS3__SR_2_RMO_AX.txt -s 6 -t 'SR_2_RMO_AX' -m 'S3' &
python verify_completion.py -o reports/reportS3__SR_2_POL_AX.txt -s 168 -t 'SR_2_POL_AX' -m 'S3' &
python verify_completion.py -o reports/reportS3A_SR_2_RGI_AX.txt -s 24 -t 'SR_2_RGI_AX' -m 'S3A' &
python verify_completion.py -o reports/reportS3B_SR_2_RGI_AX.txt -s 24 -t 'SR_2_RGI_AX' -m 'S3B' &
python verify_completion.py -o reports/reportS3__SR_2_SIC_AX.txt -s 72 -t 'SR_2_SIC_AX' -m 'S3' &
python verify_completion.py -o reports/reportS3__SL_2_SSTAAX.txt -s 24 -t 'SL_2_SSTAAX' -m 'S3' &
python verify_completion.py -o reports/reportS3A_SR_2_PCPPAX.txt -s 24 -t 'SR_2_PCPPAX' -m 'S3A' &
python verify_completion.py -o reports/reportS3B_SR_2_PCPPAX.txt -s 24 -t 'SR_2_PCPPAX' -m 'S3B' &



wait
echo "Done!"
