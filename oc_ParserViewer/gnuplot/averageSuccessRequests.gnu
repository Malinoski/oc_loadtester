# set terminal png size 800,500 enhanced font "Helvetica,20"
# set output 'output.png'

red = "#FF0000"; green = "#00FF00"; blue = "#0000FF"; skyblue = "#87CEEB";
#set yrange [0:20]
set style data histogram
set style histogram cluster gap 1
set style fill solid
set boxwidth 0.9
set xtics format "%"
set grid ytics
set ylabel "Percents"
set title "Average success requests"
set datafile separator ","
set format y '%2.0f%%' 
plot "../data/gatling-data-v7-processed.csv" using 5:xtic(1) title "vm" linecolor rgb red, \
            '' using 10 title "container" linecolor rgb blue, 
pause -1

