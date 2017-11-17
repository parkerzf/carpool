#!/bin/bash
# sh scripts/exp.sh

BASEDIR=$(dirname "$0")
NUM_INSTANCE=50

# for NUM_USER in 40 60 80 100
# do

# 	if [ "$NUM_USER" = "60" ] 
# 	then
# 		echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_C51_0.5_${NUM_USER}_0_50" 10
# 		python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_C51_0.5_${NUM_USER}_0_50" 10
# 		echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_0_50" 10
# 		python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_0_50" 10
# 		echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_1_50" 10
# 		python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_1_50" 10
# 	else
# 		echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_C51_0.5_${NUM_USER}_0_50"
# 		python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_C51_0.5_${NUM_USER}_0_50"
# 		echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_0_50"
# 		python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_0_50"
# 		echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_1_50"
# 		python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_1_50"
# 	fi

# 	python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_2_50"
# 	python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_3_50"

# 	python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_HTP/threshold_C51_0.5_${NUM_USER}_0_25"
# 	python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_HTP/threshold_R51_0.3_${NUM_USER}_0_25"
# 	python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_HTP/threshold_R51_0.3_${NUM_USER}_1_25"
# 	python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_HTP/threshold_R51_0.3_${NUM_USER}_2_25"
# 	python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_HTP/threshold_R51_0.3_${NUM_USER}_3_25"
# done

# for NUM_USER in 500 1000
for NUM_USER in 1000
do
	echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_C51_0.5_${NUM_USER}_0_50" 4
	python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_C51_0.5_${NUM_USER}_0_50" 4
	echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_0_50" 4
	python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_0_50" 4
	echo python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_1_50" 4
	python ${BASEDIR}/report.py "${BASEDIR}/../data/exp_skew/threshold_R51_0.3_${NUM_USER}_1_50" 4
done