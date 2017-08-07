import random

# assign assigns 5 people to a day
# list is the list of people
# n is the day of the weekday
# d is the day of the month, used for shuffling
def assign( list, n, d ):
	poss = [''] * 5
	x = 0
	for person in list:
		if person[n] == 'Y':
			if x < 5:
				poss[x] = person[5]
				x = x + 1
			else:
				poss = poss + [person[5]]
	result = []
	
	random.seed()
	
	# if at least 5 available, choose 5 randomly
	if len(poss) >= 5:
		for x in range(0,5):
			d = random.randint(0, len(poss)-1)
			result = result + [poss[d]]
			poss.remove(poss[d])
		return result
	# if not at least 5 available
	else:
		return poss


def mainfunction():
	print(' Hello!')
	print('  In order to create a schedule,')
	print('  I will need you to fill out the file with yard duty preferences')
	print('  As well as holiday dates')
	print('  I will also require some information which you can input right now')
	print('  When you are ready,')

	yearstr = input('\n Enter the year: ')
	month = input('\n Enter the first month: ')
	month2 = input('\n Enter the second month: ')
	lastdaystr = input('\n Enter the total number of days in ' + month + ': ')
	LASTDAY1 = int(lastdaystr)
	firstdaystr = input('\n Enter the Date of the first monday to schedule: ')
	FIRSTDAY = int(firstdaystr)

	print('\n Opening preferences.txt file')

	file_object = open('preferences.txt', 'r')

	print('\n Reading data from preferences.txt')

	list = []
	holidays = []
	year = int(yearstr)

	for line in file_object:
		splitline = line.split()
		
		# Read holidays
		if len(splitline) == 0:
			print('empty line')
		elif splitline[-1] == 'sub' or splitline[-1] == 'Sub':
			print('substitute')
		elif splitline[0] == 'Holidays:':
			if len(splitline) > 1:
				holidays = [ int(i) for i in splitline[1:] ]
		elif len(splitline) == 6:
			list = list + [splitline]
		else:
			print('ERROR! INCORRENT AMOUNT OF DATA FOR LINE: ' + line)
		
	print('\n First Day is: ' + str(FIRSTDAY))
	print(' Days in month one: ' + str(LASTDAY1))
	print(" Holidays: " + str(holidays) + '\n')
	for x in list:
		print(x)
		
	print('\n Assigning positions')

	result = []

	total = 0
	x = FIRSTDAY
	weekday = 0

	while x <= LASTDAY1:
		
		if x in holidays:
			print("in holidays")
			result = result + [[x] + [''] * 5 ]
		else:
			result = result + [ [x] + assign( list, weekday, x ) ]
		
		weekday = weekday + 1
		x = x + 1
		total = total + 1
		if weekday > 4:
			x = x + 2
			weekday = 0
			
	x = x - LASTDAY1
	added = 0
	while total < 25:
		
		toadd = []
		if added == 0:
			toadd = [ str(x) + ' ' + month2 ]
			added = 1
		else:
			toadd = [x]
		if x in holidays:
			toadd = toadd + [''] * 5
			#result = result + [ [x] + [''] * 5 ]
		else:
			toadd = toadd + assign( list, weekday, x )
			#result = result + [ [x] + assign( list, weekday, x ) ]
		
		result = result + [toadd]
		
		weekday = weekday + 1
		x = x + 1
		total = total + 1
		if weekday > 4:
			x = x + 2
			weekday = 0
		
	#for x in result:
	#	print(x)
	
	print('\n Randomizing the arrangement')	
	index = 1
	while index < len(result):
		day = result[index]
		prev = result[index-1]
		for ind in range(0, len(day)):
			person = day[ind]
			if person != '' and person == prev[ind]:
				print('shuffling')
				print('before:')
				print(prev)
				print(day)
				result[index].remove( person )
				newind = ( ind - 1 + random.randint(0,4) ) % 5 + 1
				result[index].insert(newind, person)
				print('after:')
				print(result[index-1])
				print(result[index])
				index = index - 1
				break;
		if index >= 5:
			prevweek = result[index-5]
			for ind in range(0, len(day)):
				person = day[ind]
				if person != '' and person == prevweek[ind]:
					#print('shuffling')
					#print('before:')
					#print(prevweek)
					#print(day)
					result[index].remove( person )
					newind = ( ind - 1 + random.randint(0,4) ) % 5 + 1
					result[index].insert(newind, person)
					#print('after:')
					#print(result[index-5])
					#print(result[index])
					index = index - 1
					break;
		index = index + 1
			
	
	filename = 'Mohr_' + month[0:3] + '_' + month2[0:3] + '_' + yearstr + '_Schedule.html'
	print('\n Creating ' + filename)
	target = open(filename, 'w')
	target.write('<html>\n')
	target.write('<head>\n')
	target.write('<title>' + month + '/' + month2 + ' ' + str(year) + '</title>\n')
	target.write('<style type="text/css">\n')
	target.write('th, td { padding:1px; width: 250px; font-weight: bold;font-size: 18px;}\n')
	target.write('h2 {text-align: center;}\n')
	target.write('ol { -webkit-margin-before: 5px; -webkit-margin-after: 5px; }\n')
	target.write('table, th, td {border: 1px solid black;border-collapse: collapse;}\n')
	target.write('</style>\n')
	target.write('</head>\n')

	target.write('\n<body>\n')

	target.write('<h2>Yard Duty Schedule</h2>\n')
	target.write('<h2>' + month + '/' + month2 + ' ' + str(year) + '</h2>\n')

	target.write('<table border="1">\n')
	target.write('<tr><th>Monday</th><th>Tuesday</th><th>Wednesday</th><th>Thursday</th><th>Friday</th></tr>\n')

	index = 0

	target.write('<tr>\n')
	for day in result:
		target.write('<td>\n')
		target.write( str( day[0] ) )
		target.write('<ol>\n')
		for name in day[1:]:
			target.write('<li>')
			target.write(name)
			target.write('</li>\n')
		target.write('</ol>\n')
		target.write('</td>\n')
		index = index + 1
		if index >= 5:
			target.write('</tr>\n<tr>\n')
			index = 0

	target.write('</table>\n')
	target.write('</body>\n')
	target.write('</html>\n')
	target.close()
	
	print('\n  Schedule is finished, Thank you for using this application')
	print('  Press enter to continue')
	input()

if __name__ == '__main__':
	#try:
		mainfunction()
	#except:
	#	print('Error encountered!')
	#	print('Possible Causes: non number input')
#		print ('Press Enter to close this window' )#
		#input()
