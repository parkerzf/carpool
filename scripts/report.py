from __future__ import print_function
import os, sys, glob
import numpy as np
from openpyxl import load_workbook

# python scripts/report.py dir_path
# NOTE THAT dir_path cannot come with the last slash

dir_path = sys.argv[1]

num_users = int(os.path.basename(dir_path).split('_')[3])

filepaths = [f for f in glob.glob(dir_path + '/*.xlsm')]

system_arr = np.zeros((6, len(filepaths)))
commuter_arr = np.zeros((5, len(filepaths)))
business_arr = np.zeros((5, len(filepaths)))
cpu_arr = np.zeros((1, len(filepaths)))
heuristic_arr = np.zeros((3, len(filepaths)))
appmatch_1000_arr = np.zeros((5, len(filepaths)))
appstaticmatch_1000_arr = np.zeros((5, len(filepaths)))
appstaticmatch_0_5_arr = np.zeros((5, len(filepaths)))
appstaticmatch_1_25_arr = np.zeros((5, len(filepaths)))
appstaticmatch_2_arr = np.zeros((5, len(filepaths)))
apprandommatch_1000_arr = np.zeros((5, len(filepaths)))
excel_transfer_arr = np.zeros((3, 5, len(filepaths)))
excel_matches_arr = np.zeros((3, 4, len(filepaths)))

for idx, filepath in enumerate(filepaths):

    print('process: ' + filepath)

    wb = load_workbook(filepath)
    ws_output = wb['AIMMS Output']
    ws_matches = wb['co-matches']
    ws_transfer = wb['#transfer']
    ws_algo_output = wb['Algo Output']

    # System:
    # 1) MileSav%_system = Average of [(AIMMS Output).B18/(AIMMS Output).B13]
    # 2) Time deviation_system = Average of [(AIMMS Output).B20/(AIMMS Output).B22]
    # 3) CarSav%_system = Average of [ 1 - (AIMMS Output).E16 / # of users]
    # 4) MatchingRate%_system = Average of [(AIMMS Output).B18/(AIMMS Output).B17]
    # 5) Avg#riders_system = Average of [(co-matches).B3 + 2 * (co-matches).C3 + 3 * (co-matches).D3]
    # 6) Avg#transfer_system = Average of [((#transfer).C3 + 2 * (#transfer).D3 + 3 * (#transfer).E3 + 4 * (#transfer).F3)/(AIMMS Output).B21]
    system_arr[0, idx] = 0 if ws_output['B13'].value == 0 else float(ws_output['B18'].value) / ws_output['B13'].value
    system_arr[1, idx] = 0 if ws_output['B22'].value == 0 else float(ws_output['B20'].value) / ws_output['B22'].value
    system_arr[2, idx] = 1 - float(ws_output['E16'].value) / num_users
    system_arr[3, idx] = 0 if ws_output['B17'].value == 0 else float(ws_output['B18'].value) / ws_output['B17'].value
    system_arr[4, idx] = float(ws_matches['B3'].value) + 2 * ws_matches['C3'].value + 3 * ws_matches['D3'].value
    system_arr[5, idx] = 0 if ws_output['B21'].value == 0 else \
        float(ws_transfer['C3'].value + 2 * ws_transfer['D3'].value + 3 * ws_transfer['E3'].value +
              4 * ws_transfer['F3'].value) / ws_output['B21'].value

    # Commuter:
    # 1) MileSav%_commuter = Average of [(AIMMS Output).E18/(AIMMS Output).E13]
    # 2) MatchingRate%_commuter = Average of [(AIMMS Output).E18/(AIMMS Output).E17]
    # 3) Avg#riders_commuter = Average of [(co-matches).B4 + 2 * (co-matches).C4 + 3 * (co-matches).D4]
    # 4) Time deviation_commuter = Average of [(AIMMS Output).E20/(AIMMS Output).E22]
    # 5) Avg#transfer_commuter = Average of [((#transfer).C4 + 2 * (#transfer).D4 + 3 * (#transfer).E4 + 4 * (#transfer).F4)/(AIMMS Output).E21
    commuter_arr[0, idx] = 0 if ws_output['E13'].value == 0 else float(ws_output['E18'].value) / ws_output['E13'].value
    commuter_arr[1, idx] = 0 if ws_output['E17'].value == 0 else float(ws_output['E18'].value) / ws_output['E17'].value
    commuter_arr[2, idx] = float(ws_matches['B4'].value) + 2 * ws_matches['C4'].value + 3 * ws_matches['D4'].value
    commuter_arr[3, idx] = 0 if ws_output['E22'].value == 0 else float(ws_output['E20'].value) / ws_output['E22'].value
    commuter_arr[4, idx] = 0 if ws_output['E21'].value == 0 else \
        float(ws_transfer['C4'].value + 2 * ws_transfer['D4'].value + 3 * ws_transfer['E4'].value +
              4 * ws_transfer['F4'].value) / ws_output['E21'].value

    # Business:
    # 1) MileSav%_business = Average of [(AIMMS Output).H18/(AIMMS Output).H13]
    # 2) MatchingRate%_business = Average of [(AIMMS Output).H18/(AIMMS Output).H17]
    # 3) Avg#riders_business = Average of [(co-matches).B5 + 2 * (co-matches).C5 + 3 * (co-matches).D5]
    # 4) Time deviation_business = Average of [(AIMMS Output).H20/(AIMMS Output).H22]
    # 5) Avg#transfer_business = Average of [((#transfer).C5 + 2 * (#transfer).D5 + 3 * (#transfer).E5 + 4 * (#transfer).F5)/(AIMMS Output).H21]
    business_arr[0, idx] = 0 if ws_output['H13'].value == 0 else float(ws_output['H18'].value) / ws_output['H13'].value
    business_arr[1, idx] = 0 if ws_output['H17'].value == 0 else float(ws_output['H18'].value) / ws_output['H17'].value
    business_arr[2, idx] = float(ws_matches['B5'].value) + 2 * ws_matches['C5'].value + 3 * ws_matches['D5'].value
    business_arr[3, idx] = 0 if ws_output['H22'].value == 0 else float(ws_output['H20'].value) / ws_output['H22'].value
    business_arr[4, idx] = 0 if ws_output['H21'].value == 0 else \
        float(ws_transfer['C5'].value + 2 * ws_transfer['D5'].value + 3 * ws_transfer['E5'].value +
              4 * ws_transfer['F5'].value) / ws_output['H21'].value

    # CPU time = Average of [(AIMMS Output).B10]
    cpu_arr[0, idx] = float(ws_output['B10'].value)

    # Heuristic:
    # Objective = Average of [ (heuristic queue sol - (AIMMS Output).B16) / (0.5 * (AIMMS Output).B13 - (AIMMS Output).B16) ]
    # Objective = Average of [ (heuristic sortlist sol - (AIMMS Output).B16) / (0.5 * (AIMMS Output).B13 - (AIMMS Output).B16) ]
    # Objective = Average of [ (heuristic random 10 sol - (AIMMS Output).B16) / (0.5 * (AIMMS Output).B13 - (AIMMS Output).B16) ]
    queue_sol = ws_algo_output['E2'].value
    sortlist_sol = ws_algo_output['E3'].value
    random_10_sol = ws_algo_output['E7'].value
    heuristic_arr[0, idx] = 0 if (0.5 * ws_output['B13'].value - ws_output['B16'].value == 0) else \
        (queue_sol - ws_output['B16'].value) / (0.5 * ws_output['B13'].value - ws_output['B16'].value)
    heuristic_arr[1, idx] = 0 if (0.5 * ws_output['B13'].value - ws_output['B16'].value == 0) else \
        (sortlist_sol - ws_output['B16'].value) / (0.5 * ws_output['B13'].value - ws_output['B16'].value)
    heuristic_arr[2, idx] = 0 if (0.5 * ws_output['B13'].value - ws_output['B16'].value == 0) else \
        (random_10_sol - ws_output['B16'].value) / (0.5 * ws_output['B13'].value - ws_output['B16'].value)

    appmatch_1000_arr[0, idx] = ws_algo_output['D2'].value
    appmatch_1000_arr[1, idx] = ws_algo_output['E2'].value
    appmatch_1000_arr[2, idx] = ws_algo_output['F2'].value
    appmatch_1000_arr[3, idx] = ws_algo_output['G2'].value
    appmatch_1000_arr[4, idx] = ws_algo_output['H2'].value

    appstaticmatch_1000_arr[0, idx] = ws_algo_output['D3'].value
    appstaticmatch_1000_arr[1, idx] = ws_algo_output['E3'].value
    appstaticmatch_1000_arr[2, idx] = ws_algo_output['F3'].value
    appstaticmatch_1000_arr[3, idx] = ws_algo_output['G3'].value
    appstaticmatch_1000_arr[4, idx] = ws_algo_output['H3'].value

    appstaticmatch_0_5_arr[0, idx] = ws_algo_output['D4'].value
    appstaticmatch_0_5_arr[1, idx] = ws_algo_output['E4'].value
    appstaticmatch_0_5_arr[2, idx] = ws_algo_output['F4'].value
    appstaticmatch_0_5_arr[3, idx] = ws_algo_output['G4'].value
    appstaticmatch_0_5_arr[4, idx] = ws_algo_output['H4'].value

    appstaticmatch_1_25_arr[0, idx] = ws_algo_output['D5'].value
    appstaticmatch_1_25_arr[1, idx] = ws_algo_output['E5'].value
    appstaticmatch_1_25_arr[2, idx] = ws_algo_output['F5'].value
    appstaticmatch_1_25_arr[3, idx] = ws_algo_output['G5'].value
    appstaticmatch_1_25_arr[4, idx] = ws_algo_output['H5'].value

    appstaticmatch_2_arr[0, idx] = ws_algo_output['D6'].value
    appstaticmatch_2_arr[1, idx] = ws_algo_output['E6'].value
    appstaticmatch_2_arr[2, idx] = ws_algo_output['F6'].value
    appstaticmatch_2_arr[3, idx] = ws_algo_output['G6'].value
    appstaticmatch_2_arr[4, idx] = ws_algo_output['H6'].value

    apprandommatch_1000_arr[0, idx] = ws_algo_output['D7'].value
    apprandommatch_1000_arr[1, idx] = ws_algo_output['E7'].value
    apprandommatch_1000_arr[2, idx] = ws_algo_output['F7'].value
    apprandommatch_1000_arr[3, idx] = ws_algo_output['G7'].value
    apprandommatch_1000_arr[4, idx] = ws_algo_output['H7'].value

    # Excel:
    # (#transfer).B3 --> (#transfer).F5: compute their average
    # (co-matches).B3 --> (co-matches).E5: compute their average
    for i in range(3, 6):
        for j in range(66, 71):
            excel_transfer_arr[i-3, j-66, idx] = ws_transfer['%c%d' % (j, i)].value

    for i in range(3, 6):
        for j in range(66, 70):
            excel_matches_arr[i-3, j-66, idx] = ws_matches['%c%d' % (j, i)].value

np.set_printoptions(suppress=True, precision=4)
print('System:')
print(np.average(system_arr, axis=1))
print('Commuter:')
print(np.average(commuter_arr, axis=1))
print('Business:')
print(np.average(business_arr, axis=1))
print('CPU time:')
print(np.average(cpu_arr, axis=1))
print('Heuristic')
print(np.average(heuristic_arr, axis=1))
print('appmatch 1000')
print(np.average(appmatch_1000_arr, axis=1))
print('appstaticmatch 1000')
print(np.average(appstaticmatch_1000_arr, axis=1))
print('appstaticmatch 0.5')
print(np.average(appstaticmatch_0_5_arr, axis=1))
print('appstaticmatch 1.25')
print(np.average(appstaticmatch_1_25_arr, axis=1))
print('appstaticmatch 2')
print(np.average(appstaticmatch_2_arr, axis=1))
print('apprandommatch 1000, 10 times')
print(np.average(apprandommatch_1000_arr, axis=1))
print('Excel:')
print(np.average(excel_transfer_arr, axis=2))
print(np.average(excel_matches_arr, axis=2))
