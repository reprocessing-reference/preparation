

class RequestGenerator:

    def __init__(self):
        self.domain = "g"
        self.dataset = None
        self.repres = "gg"
        self.gaussian = "reduced"
        self.grid = "640"
        self.classid = "od"
        self.date_begin = "2015-06-23"
        self.date_end = "2015-06-30"
        self.expver = "1"
        self.levelist = "1000"
        self.levtype = "sfc"
        self.param_list = ["206.128", "137.128" , "151.128" ]
        self.step = ["9","12","15","18","21","24","27","30","33","36","39","42","45","48"]
        self.stream = "oper"
        self.time_list = ["00:00:00","12:00:00"]
        self.type = "fc"
        self.target = "2015-06/result1.grib"

    def concat_list(lst):
        if len(lst) == 0:
            return ""
        else:
            res = lst[0]
            for f in range(1,len(lst)):
                res = res + "/"+lst[f]
            return res

    def write_to_file(self, filename):
        with open(filename,"w") as f:
            f.write("retrieve,")
            f.write("\n")
            if self.domain:
                f.write("  domain="+self.domain+",")
                f.write("\n")
                f.write("  repres="+self.repres+",")
                f.write("\n")
                f.write("  gaussian="+self.gaussian+",")
                f.write("\n")
            if self.dataset:
                f.write("  dataset=" + self.dataset + ",")
                f.write("\n")
            f.write("  grid="+self.grid+",")
            f.write("\n")
            f.write("  class="+self.classid+",")
            f.write("\n")
            if self.date_end:
                f.write("  date="+self.date_begin+"/to/"+self.date_end+",")
            else:
                f.write("  date=" + self.date_begin + ",")
            f.write("\n")
            f.write("  expver="+self.expver+",")
            f.write("\n")
            f.write("  levelist="+self.levelist+",")
            f.write("\n")
            f.write("  levtype="+self.levtype+",")
            f.write("\n")
            f.write("  param="+RequestGenerator.concat_list(self.param_list)+",")
            f.write("\n")
            if self.step:
                f.write("  step="+RequestGenerator.concat_list(self.step)+",")
                f.write("\n")
            f.write("  stream="+self.stream+",")
            f.write("\n")
            f.write("  time="+RequestGenerator.concat_list(self.time_list)+",")
            f.write("\n")
            f.write("  type="+self.type+",")
            f.write("\n")
            f.write("  target=\""+self.target+"\"")
            f.write("\n")
            f.close()



