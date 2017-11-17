from __future__ import print_function
import os, sys, glob
import numpy as np
from openpyxl import load_workbook

# python scripts/report.py dir_path
# NOTE THAT dir_path cannot come with the last slash

dir_path = sys.argv[1]

if len(sys.argv) == 3:
    max_row = int(sys.argv[2])
else:
    max_row = 7

base_path = os.path.basename(dir_path)

num_instances = 50

filepaths = [f for f in glob.glob(dir_path + '/*.xlsm')]

out_path = dir_path + '/'  + base_path + '_stats.txt'
f = open(out_path, 'w')

cpu_arr = np.zeros((1, len(filepaths)))

heuristic_arr = np.zeros((6, len(filepaths))) if max_row == 10 else np.zeros((3, len(filepaths)))

appmatch_1000_arr = np.zeros((5, len(filepaths)))
appstaticmatch_1000_arr = np.zeros((5, len(filepaths)))
appstaticmatch_0_5_arr = np.zeros((5, len(filepaths)))
appstaticmatch_1_25_arr = np.zeros((5, len(filepaths)))
appstaticmatch_2_arr = np.zeros((5, len(filepaths)))
apprandommatch_1000_10_arr = np.zeros((5, len(filepaths)))
apprandommatch_1000_1_arr = np.zeros((5, len(filepaths)))
apprandommatch_1000_5_arr = np.zeros((5, len(filepaths)))
appstatictaximatch_1000_arr = np.zeros((5, len(filepaths)))

for idx, filepath in enumerate(filepaths):
    wb = load_workbook(filepath)

    if 'Algo Output' not in wb.sheetnames:
        f.write('Missing sheet: ' + filepath + '\n')
        num_instances -= 1
        continue

    ws_output = wb['AIMMS Output']
    ws_matches = wb['co-matches']
    ws_transfer = wb['#transfer']
    ws_algo_output = wb['Algo Output']

    row_count = ws_algo_output.max_row


    if row_count < max_row:
        f.write('Missing row: ' + filepath + '\n')
        num_instances -= 1
        continue


    try:
        # CPU time = Average of [(AIMMS Output).B10]
        cpu_arr[0, idx] = float(ws_output['B10'].value)

        # Heuristic:
        # Objective = Average of [ (heuristic queue sol - (AIMMS Output).B16) / (0.5 * (AIMMS Output).B13 - (AIMMS Output).B16) ]
        # Objective = Average of [ (heuristic sortlist sol - (AIMMS Output).B16) / (0.5 * (AIMMS Output).B13 - (AIMMS Output).B16) ]
        # Objective = Average of [ (heuristic random 10 sol - (AIMMS Output).B16) / (0.5 * (AIMMS Output).B13 - (AIMMS Output).B16) ] 
        if max_row != 4:
            queue_sol = ws_algo_output['E2'].value
            sortlist_sol = ws_algo_output['E3'].value
            random_10_sol = ws_algo_output['E7'].value
            heuristic_arr[0, idx] = 0 if (0.5 * ws_output['B13'].value - ws_output['B16'].value == 0) else \
                (queue_sol - ws_output['B16'].value) / (0.5 * ws_output['B13'].value - ws_output['B16'].value)
            heuristic_arr[1, idx] = 0 if (0.5 * ws_output['B13'].value - ws_output['B16'].value == 0) else \
                (sortlist_sol - ws_output['B16'].value) / (0.5 * ws_output['B13'].value - ws_output['B16'].value)
            heuristic_arr[2, idx] = 0 if (0.5 * ws_output['B13'].value - ws_output['B16'].value == 0) else \
                (random_10_sol - ws_output['B16'].value) / (0.5 * ws_output['B13'].value - ws_output['B16'].value)
        if max_row == 10:
            random_1_sol = ws_algo_output['E8'].value
            random_5_sol = ws_algo_output['E9'].value
            taxi_sol = ws_algo_output['E10'].value
            heuristic_arr[3, idx] = 0 if (0.5 * ws_output['B13'].value - ws_output['B16'].value == 0) else \
            (random_1_sol - ws_output['B16'].value) / (0.5 * ws_output['B13'].value - ws_output['B16'].value)
            heuristic_arr[4, idx] = 0 if (0.5 * ws_output['B13'].value - ws_output['B16'].value == 0) else \
            (random_5_sol - ws_output['B16'].value) / (0.5 * ws_output['B13'].value - ws_output['B16'].value)
            heuristic_arr[5, idx] = 0 if (0.5 * ws_output['B13'].value - ws_output['B16'].value == 0) else \
            (taxi_sol - ws_output['B16'].value) / (0.5 * ws_output['B13'].value - ws_output['B16'].value)

        if max_row != 4:
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

            apprandommatch_1000_10_arr[0, idx] = ws_algo_output['D7'].value
            apprandommatch_1000_10_arr[1, idx] = ws_algo_output['E7'].value
            apprandommatch_1000_10_arr[2, idx] = ws_algo_output['F7'].value
            apprandommatch_1000_10_arr[3, idx] = ws_algo_output['G7'].value
            apprandommatch_1000_10_arr[4, idx] = ws_algo_output['H7'].value

        if max_row == 10:
            apprandommatch_1000_1_arr[0, idx] = ws_algo_output['D8'].value
            apprandommatch_1000_1_arr[1, idx] = ws_algo_output['E8'].value
            apprandommatch_1000_1_arr[2, idx] = ws_algo_output['F8'].value
            apprandommatch_1000_1_arr[3, idx] = ws_algo_output['G8'].value
            apprandommatch_1000_1_arr[4, idx] = ws_algo_output['H8'].value

            apprandommatch_1000_5_arr[0, idx] = ws_algo_output['D9'].value
            apprandommatch_1000_5_arr[1, idx] = ws_algo_output['E9'].value
            apprandommatch_1000_5_arr[2, idx] = ws_algo_output['F9'].value
            apprandommatch_1000_5_arr[3, idx] = ws_algo_output['G9'].value
            apprandommatch_1000_5_arr[4, idx] = ws_algo_output['H9'].value

            appstatictaximatch_1000_arr[0, idx] = ws_algo_output['D10'].value
            appstatictaximatch_1000_arr[1, idx] = ws_algo_output['E10'].value
            appstatictaximatch_1000_arr[2, idx] = ws_algo_output['F10'].value
            appstatictaximatch_1000_arr[3, idx] = ws_algo_output['G10'].value
            appstatictaximatch_1000_arr[4, idx] = ws_algo_output['H10'].value

        if max_row == 3:
            apprandommatch_1000_1_arr[0, idx] = ws_algo_output['D2'].value
            apprandommatch_1000_1_arr[1, idx] = ws_algo_output['E2'].value
            apprandommatch_1000_1_arr[2, idx] = ws_algo_output['F2'].value
            apprandommatch_1000_1_arr[3, idx] = ws_algo_output['G2'].value
            apprandommatch_1000_1_arr[4, idx] = ws_algo_output['H2'].value

            apprandommatch_1000_5_arr[0, idx] = ws_algo_output['D3'].value
            apprandommatch_1000_5_arr[1, idx] = ws_algo_output['E3'].value
            apprandommatch_1000_5_arr[2, idx] = ws_algo_output['F3'].value
            apprandommatch_1000_5_arr[3, idx] = ws_algo_output['G3'].value
            apprandommatch_1000_5_arr[4, idx] = ws_algo_output['H3'].value

            appstatictaximatch_1000_arr[0, idx] = ws_algo_output['D4'].value
            appstatictaximatch_1000_arr[1, idx] = ws_algo_output['E4'].value
            appstatictaximatch_1000_arr[2, idx] = ws_algo_output['F4'].value
            appstatictaximatch_1000_arr[3, idx] = ws_algo_output['G4'].value
            appstatictaximatch_1000_arr[4, idx] = ws_algo_output['H4'].value


    except:
        print(filepath)


np.set_printoptions(suppress=True, precision=4)

f.write('Number of instances: '+ str(num_instances) + '\n')

if num_instances != 0:
    if max_row != 4:
        f.write('CPU time:'+ '\n')
        f.write(np.array_str(np.sum(cpu_arr, axis=1)/num_instances) + '\n')
        f.write('Heuristic'+ '\n')
        f.write(np.array_str(np.sum(heuristic_arr, axis=1)/num_instances)+ '\n')
        f.write('appmatch 1000'+ '\n')
        f.write(np.array_str(np.sum(appmatch_1000_arr, axis=1)/num_instances)+ '\n')
        f.write('appstaticmatch 1000'+ '\n')
        f.write(np.array_str(np.sum(appstaticmatch_1000_arr, axis=1)/num_instances)+ '\n')
        f.write('appstaticmatch 0.5'+ '\n')
        f.write(np.array_str(np.sum(appstaticmatch_0_5_arr, axis=1)/num_instances)+ '\n')
        f.write('appstaticmatch 1.25'+ '\n')
        f.write(np.array_str(np.sum(appstaticmatch_1_25_arr, axis=1)/num_instances)+ '\n')
        f.write('appstaticmatch 2'+ '\n')
        f.write(np.array_str(np.sum(appstaticmatch_2_arr, axis=1)/num_instances)+ '\n')
        f.write('apprandommatch 1000, 10 times'+ '\n')
        f.write(np.array_str(np.sum(apprandommatch_1000_10_arr, axis=1)/num_instances)+ '\n')
    if max_row != 7:
        f.write('apprandommatch 1000, 1 times'+ '\n')
        f.write(np.array_str(np.sum(apprandommatch_1000_1_arr, axis=1)/num_instances)+ '\n')
        f.write('apprandommatch 1000, 5 times'+ '\n')
        f.write(np.array_str(np.sum(apprandommatch_1000_5_arr, axis=1)/num_instances)+ '\n')
        f.write('appstatictaximatch 1000'+ '\n')
        f.write(np.array_str(np.sum(appstatictaximatch_1000_arr, axis=1)/num_instances)+ '\n')

f.close()
