# sh scripts/exp.sh

BASEDIR=$(dirname "$0")
NUM_INSTANCE=50

# for NUM_USER in 40 60 80 100
# do
# 	${BASEDIR}/sub1.sh exp_skew threshold_C51_0.5 ${NUM_USER} 0 50 ${NUM_INSTANCE}
# 	${BASEDIR}/sub1.sh exp_skew threshold_R51_0.3 ${NUM_USER} 0 50 ${NUM_INSTANCE}
# 	${BASEDIR}/sub1.sh exp_skew threshold_R51_0.3 ${NUM_USER} 1 50 ${NUM_INSTANCE}
# 	${BASEDIR}/sub1.sh exp_skew threshold_R51_0.3 ${NUM_USER} 2 50 ${NUM_INSTANCE}
# 	${BASEDIR}/sub1.sh exp_skew threshold_R51_0.3 ${NUM_USER} 3 50 ${NUM_INSTANCE}

# 	${BASEDIR}/sub1.sh exp_HTP threshold_C51_0.5 ${NUM_USER} 0 25 ${NUM_INSTANCE}
# 	${BASEDIR}/sub1.sh exp_HTP threshold_R51_0.3 ${NUM_USER} 0 25 ${NUM_INSTANCE}
# 	${BASEDIR}/sub1.sh exp_HTP threshold_R51_0.3 ${NUM_USER} 1 25 ${NUM_INSTANCE}
# 	${BASEDIR}/sub1.sh exp_HTP threshold_R51_0.3 ${NUM_USER} 2 25 ${NUM_INSTANCE}
# 	${BASEDIR}/sub1.sh exp_HTP threshold_R51_0.3 ${NUM_USER} 3 25 ${NUM_INSTANCE}
# done

# for NUM_USER in 60
# do
# 	${BASEDIR}/sub1_1.sh exp_skew threshold_C51_0.5 ${NUM_USER} 0 50 ${NUM_INSTANCE}
# 	${BASEDIR}/sub1_1.sh exp_skew threshold_R51_0.3 ${NUM_USER} 0 50 ${NUM_INSTANCE}
# 	${BASEDIR}/sub1_1.sh exp_skew threshold_R51_0.3 ${NUM_USER} 1 50 ${NUM_INSTANCE}
# done


for NUM_USER in 500 1000
do
	${BASEDIR}/sub1_2.sh exp_skew threshold_C51_0.5 ${NUM_USER} 0 50 ${NUM_INSTANCE}
	${BASEDIR}/sub1_2.sh exp_skew threshold_R51_0.3 ${NUM_USER} 0 50 ${NUM_INSTANCE}
done