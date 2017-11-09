# sh scripts/sub2.sh AppMatch exp_skew threshold_C51_0.5 100 0 50 20 1000 1
APP=$1
DATA_DIR=$2
GRAPH_BASE=$3
NUM_USER=$4
NUM_SPOT=$5
NUM_NODE=$6
NUM_INSTANCE=$7
TAXI_COST=$8
NUM_ITER=$9

BASEDIR=$(dirname "$0")

CMD="java -cp ${BASEDIR}/../target/2016_05_carpool-1.0-SNAPSHOT.jar nl.twente.bms."$APP

for ((seq = 1; seq <= $NUM_INSTANCE; seq++));
	do
		$CMD "${BASEDIR}/../data/${GRAPH_BASE}.graphml" "${BASEDIR}/../data/${DATA_DIR}/${GRAPH_BASE}_${NUM_USER}_${NUM_SPOT}_${NUM_NODE}/${GRAPH_BASE}_${NUM_USER}_${NUM_SPOT}_${NUM_NODE}_${seq}.txt" ${TAXI_COST} ${NUM_ITER}
done
