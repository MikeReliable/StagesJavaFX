package sample;

public class Point {

    double strainRelative;
    double lnStrainTrue;
    double trueStress;

    public double getStrainRelative() {
        return strainRelative;
    }

    public void setStrainRelative(double strainRelative) {
        this.strainRelative = strainRelative;
    }

    public double getLnStrainTrue() {
        return lnStrainTrue;
    }

    public void setLnStrainTrue(double lnStrainTrue) {
        this.lnStrainTrue = lnStrainTrue;
    }

    public double getTrueStress() {
        return trueStress;
    }

    public void setTrueStress(double trueStress) {
        this.trueStress = trueStress;
    }
}
