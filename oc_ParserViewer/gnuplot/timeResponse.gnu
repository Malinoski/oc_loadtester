# set terminal png size 800,500 enhanced font "Helvetica,20"
# set output 'output.png'

red = "#FF0000"; green = "#00FF00"; blue = "#0000FF"; skyblue = "#87CEEB";
set yrange [0:]
set style data histogram
set style histogram errorbars gap 1 lw 1
set style fill pattern border -1
set boxwidth 0.9
# set ytics format "%"
set grid ytics
set ylabel "Seconds"

containerFile = '../data/ro30ra10us100-200-400-600-800-1000-processed-cont.csv'
vmFile = '../data/ro30ra10us100-200-400-600-800-1000-processed-vm.csv'

containerNote = system("head -1 ".containerFile)
vmNote = system("head -1 ".vmFile)
set title "Average time response\n\n ".containerNote."\n".vmNote

set datafile separator ","
# set format y '%' 
plot containerFile using 2:7:xtic(1) title "container" linecolor rgb blue, \
     vmFile        using 2:7 title "vm" linecolor rgb red, 
pause -1

