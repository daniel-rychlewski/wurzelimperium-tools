import java.util.Optional;

public class Produkt {
    int preis;
    Ort ort;
    Ausrichtung ausrichtung;
    Gartenart gartenart;
    public Produkt(int preis, Ort ort, Ausrichtung ausrichtung, Gartenart gartenart) {
        this.preis = preis;
        this.ort = ort;
        this.ausrichtung = ausrichtung;
        this.gartenart = gartenart;
    }
}
