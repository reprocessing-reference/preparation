#!/bin/sh
template_home=$(dirname $(readlink -f $0))

paper=a4paper
hmargin=3cm
vmargin=3.5cm
fontsize=10pt
#fontsize=11pt
#fontsize=12pt

#mainfont=Cambria
#sansfont=Corbel
#monofont=Consolas
mainfont=Georgia
sansfont=Verdana
monofont="Courier New"
language=english
#language=swedish
nohyphenation=false

columns=onecolumn
#columns=twocolumn

geometry=portrait
#geometry=landscape

#alignment=flushleft
#alignment=flushright
#alignment=center
cd /home/1000/website/data/docs
for fichier in *.docx
do
    echo Converting $fichier
    mkdir $(basename $fichier .${fichier##*.})

    cd $(basename $fichier .${fichier##*.})
    pandoc -s -c ../../../assets/css/styledoc.css -A ../../../_includes/footer.html -o $(basename $fichier .${fichier##*.}).html --extract-media=./media ../$(basename $fichier .${fichier##*.}).docx --metadata pagetitle="..."
    cd ..
done




