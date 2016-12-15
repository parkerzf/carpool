import xlrd
import sys

def read_num_cars(path):
  workbook = xlrd.open_workbook(path)
  sheet = workbook.sheet_by_name('AIMMS Output')
  return sheet.cell(15,4)


prefix = sys.argv[1]
num_hotspots = sys.argv[2]
num_users = sys.argv[3]
num_cities = sys.argv[4]

sum = 0
cnt = 0
for seq in range(1, 11):
  path = '_'.join([prefix, num_hotspots, str(seq), num_users, num_cities]) + '.xlsm'
  num_cars = read_num_cars(path)
  if num_cars.ctype == 2:
    sum += num_cars.value
    cnt += 1



path_pattern = '_'.join([prefix.split('/')[-1], num_hotspots, 'x', num_users, num_cities])
print path_pattern, sum * 1.0/cnt