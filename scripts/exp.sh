# sh scripts/exp.sh

BASEDIR=$(dirname "$0")

for NUM_USER in 40 60 80 100
do
	${BASEDIR}/sub1.sh exp_skew threshold_C51_0.5 ${NUM_USER} 0 50 20
	${BASEDIR}/sub1.sh exp_skew threshold_R51_0.3 ${NUM_USER} 0 50 20
	${BASEDIR}/sub1.sh exp_skew threshold_R51_0.3 ${NUM_USER} 1 50 20
	${BASEDIR}/sub1.sh exp_skew threshold_R51_0.3 ${NUM_USER} 2 50 20
	${BASEDIR}/sub1.sh exp_skew threshold_R51_0.3 ${NUM_USER} 3 50 20

	${BASEDIR}/sub1.sh exp_HTP threshold_C51_0.5 ${NUM_USER} 0 25 20
	${BASEDIR}/sub1.sh exp_HTP threshold_R51_0.3 ${NUM_USER} 0 25 20
	${BASEDIR}/sub1.sh exp_HTP threshold_R51_0.3 ${NUM_USER} 1 25 20
	${BASEDIR}/sub1.sh exp_HTP threshold_R51_0.3 ${NUM_USER} 2 25 20
	${BASEDIR}/sub1.sh exp_HTP threshold_R51_0.3 ${NUM_USER} 3 25 20
done