Shakkiappi - Moderni shakkikello Androidille

Nykyaikainen shakkikello-sovellus Androidille, jossa on laajat ominaisuudet ja moderni käyttöliittymä.

Ominaisuudet

- Shakkikello: Kaksi painiketta (koko näyttö jaettuna kahtia)
- Ajanvalinta: 3 min, 5 min, 10 min + custom-aika (minuutit ja sekunnit)
- Lisäys ja viive: Fischer vs Bronstein -aikakontrollit
- Ääni- ja tuntopalautteet: Painalluksista saa välittömän palautteen
- Kierroslaskuri: Montako siirtoa kummallakin pelaajalla
- Visuaaliset teemat: Vaihdettavat väriteemat
- Pelitilastot: Historia ja voittoprosentit
- Monipelaaja: Bluetooth-tuki kahdelle laitteelle (tulossa)

Teknologiat

- Kotlin 1.9.20 - Pääohjelmointikieli
- Jetpack Compose 1.5.4 - Deklaratiivinen UI
- Clean Architecture + MVVM - Skaalautuva arkkitehtuuri
- Room 2.6.1 - Paikallinen tietokanta
- Hilt 2.48 - Riippuvuuksien injektio
- DataStore 1.0.0 - Asetusten tallennus
- ExoPlayer 1.2.1 - Ääniefektit
- Navigation Compose 2.7.6 - Navigointi

Projektin rakenne

app/src/main/java/com/example/shakkiappi/
├── data/                    # Data layer
│   ├── local/              # Paikallinen data
│   │   ├── database/       # Room-tietokanta
│   │   ├── datastore/      # DataStore-asetukset
│   │   └── repository/     # Repository-toteutukset
│   └── model/              # Data-modelit
├── domain/                  # Domain layer
│   ├── repository/         # Repository-rajapinnat
│   └── usecase/            # Use case -luokat
├── presentation/            # Presentation layer
│   ├── ui/                 # UI-komponentit
│   │   ├── components/     # Uudelleenkäytettävät komponentit
│   │   ├── screens/        # Näkymät
│   │   └── theme/          # Teemat
│   └── viewmodel/          # ViewModel-luokat
├── service/                 # Palvelut (äänet, haptiikka)
└── utils/                   # Apufunktiot

Asennus

1. Kloonaa repositorio:
   git clone https://github.com/Xapeck/shakkiappi.git
   cd shakkiappi

2. Avaa projekti Android Studiossa:
   File -> Open -> Valitse projekti

3. Synkronoi Gradle:
   File -> Sync Project with Gradle Files

4. Lisää äänitiedostot:
   app/src/main/res/raw/click_sound.mp3 (lyhyt napsahdus)
   app/src/main/res/raw/game_end.mp3 (pelin päättymisääni)

5. Buildaa ja aja:
   Valitse emulaattori tai fyysinen laite
   Paina Run (Shift + F10)

Käyttöohje

1. Pelin aloitus: Paina oikean alareunan asetusnappia
2. Ajan valinta: Valitse esiasetettu aika tai custom-aika
3. Pelaaminen: Paina omaa puoliskoa näytöstä siirron jälkeen
4. Tilastot: Vasemman alareunan tähtinappi näyttää pelihistorian

Branch-strategia

- main - Tuotantoversio, vain stabiilit julkaisut
- dev - Kehityshaara, uudet ominaisuudet

Osallistuminen

1. Forkkaa repositorio
2. Luo uusi feature-haara (git checkout -b feature/ominaisuus)
3. Committaa muutokset (git commit -m 'Lisää uusi ominaisuus')
4. Pushaa haara (git push origin feature/ominaisuus)
5. Avaa Pull Request

Lisenssi

MIT License

Tekijät

Xapeck - https://github.com/Xapeck

Starraa repositorio, jos pidit projektista!
