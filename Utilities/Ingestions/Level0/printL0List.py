# coding: utf-8

#
# This Script allows you to print all the content of each file containing L0 names,
# only if the year & month of the file (given in its name) are greater than the year
# and month given in parameter.
#
# Example of script launching command :
# python -u printL0List.py -y 2021 -m 10 -i S1 > S1_L0_202110_202205.listing
# It will list all the L0 from October 2021 to the last file in the S1 directory.
# The output of the print is redirected to another file to list every L0 of the period.
#

import glob
import argparse

def listL0(year, month, inputDirPath):
    # Récupération de tous les fichiers dans le répertoire source
    listing = glob.glob(inputDirPath)
    for l in listing:
        # Récupérer le mois et l'année des L0 contenus dans le fichier depuis le nom du fichier
        monthOfFile = l[15:17]
        yearOfFile = l[18:22]

        if int(yearOfFile) < int(year) or (int(yearOfFile) == int(year) and int(monthOfFile) < int(month)):
            # La date du fichier est inférieure à la date ciblée
            continue
        else:
            # La date du fichier est supérieure ou égale à la date depuis laquelle on veut générer le listing
            with open( l ,"r") as fid:
                lines = fid.readlines()

                # Affichage de tous les noms de L0 contenus dans le fichier dont la date correspond
                for aux in lines:
                    print(aux.split('\n')[0])

if __name__ == "__main__":

    parser = argparse.ArgumentParser(description="This script allows you to print all the L0 of a directory from a given month and year",  # main description for help
            epilog='\n\n', formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
    parser.add_argument("-y", "--year",
                        help="Year",
                        required=True)
    parser.add_argument("-m", "--month",
                        help="Month",
                        required=True)
    parser.add_argument("-i", "--inputDir",
                        help="Dossier qui contient un listing des fichiers L0 par mois nommes au format SX_L0_names_mm_yyyy_*.txt",
                        required=True)

    args = parser.parse_args()
    
    listL0(args.year, args.month, args.inputDir + "/*.txt")