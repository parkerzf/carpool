# sh scripts/exp.sh

BASEDIR=$(dirname "$0")
NUM_INSTANCE=50

for NUM_USER in 40 60 80 100
do
	# echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_C51_0.5_${NUM_USER}_0_50"
	# python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_C51_0.5_${NUM_USER}_0_50"
	# echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_0_50"
	# python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_0_50"
	# echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_1_50"
	# python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_1_50"
	# echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_2_50"
	# python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_2_50"
	# echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_3_50"
	# python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_3_50"

	echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_HTP/threshold_C51_0.5_${NUM_USER}_0_25"
	python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_HTP/threshold_C51_0.5_${NUM_USER}_0_25"
	echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_HTP/threshold_R51_0.3_${NUM_USER}_0_25"
	python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_HTP/threshold_R51_0.3_${NUM_USER}_0_25"
	echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_HTP/threshold_R51_0.3_${NUM_USER}_1_25"
	python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_HTP/threshold_R51_0.3_${NUM_USER}_1_25"
	echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_HTP/threshold_R51_0.3_${NUM_USER}_2_25"
	python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_HTP/threshold_R51_0.3_${NUM_USER}_2_25"
	echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_HTP/threshold_R51_0.3_${NUM_USER}_3_25"
	python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_HTP/threshold_R51_0.3_${NUM_USER}_3_25"
done