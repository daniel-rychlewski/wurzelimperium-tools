public enum Ort {
    WURZELTAL {
        @Override
        Wochentag[] verfuegbar(boolean premium) {
            return Wochentag.values();
        }
    }, SCHREBERLINGEN {
        @Override
        Wochentag[] verfuegbar(boolean premium) {
            if (premium) {
                // Auto fährt bei Bedarf an jedem Wochentag nach Schreberlingen
                return Wochentag.values();
            }
            else {
                // Bus fährt nur am Mittwoch und Samstag nach Schreberlingen
                return new Wochentag[]{Wochentag.MITTWOCH, Wochentag.SAMSTAG};
            }
        }
    };
    abstract Wochentag[] verfuegbar(boolean premiumAccountAktiviert);
}
