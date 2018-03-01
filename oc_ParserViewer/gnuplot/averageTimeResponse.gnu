# set terminal png size 800,500 enhanced font "Helvetica,20"
# set output 'output.png'

red = "#FF0000"; green = "#00FF00"; blue = "#0000FF"; skyblue = "#87CEEB";
#set yrange [0:20]
set style data histogram
set style histogram cluster gap 1
set style fill solid
set boxwidth 0.9
# set xtics format "sec"
set grid ytics
set ylabel "Seconds"
set title "Average time response"
set datafile separator ","
# set format y '%2.0f%%' 
plot "../data/gatling-data-v7-processed.csv" using 2:xtic(1) title "vm" linecolor rgb red, \
                                     '' using 7 title "container" linecolor rgb blue, 
pause -1

