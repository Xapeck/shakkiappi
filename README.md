# Shakkiappi - Moderni shakkikello Androidille

Nykyaikainen shakkikello-sovellus Androidille, jossa on intuitiivinen käyttöliittymä ja kattavat ominaisuudet.

## Ominaisuudet

- Shakkikello - Koko näyttö jaettuna kahtia, paina omaa puoliskoasi siirron jälkeen
- Ajanvalinta - Esiasetukset: 1 min (Bullet), 3 min (Blitz), 5 min (Rapid), 10 min (Classical), 15 min
- Increment (Fischer) - Lisäysaika jokaisen siirron jälkeen: 3+2, 5+3, 10+5
- Siirtolaskuri - Seuraa kummankin pelaajan siirtoja
- Tuntopalaute - Värinä jokaisesta painalluksesta
- RESET - Pitkä painallus palauttaa ajanvalintatilaan
- ASETUKSET - Vaihda aikakontrollia kesken pelin (pausettaa ajan)
- Tauko-toiminto - Aika pysähtyy asetusten ajaksi
- JATKA PELIÄ - Jatka tauon jälkeen
- Visuaalinen palaute - Aktiivinen pelaaja näkyy vihreällä

## Teknologiat

- Kotlin 1.9.20 - Pääohjelmointikieli
- Jetpack Compose 1.5.4 - Deklaratiivinen UI
- ViewModel 2.7.0 - Tilojen hallinta
- Coroutines 1.7.3 - Ajanlasku ja taustatyöt
- Material 3 - Moderni UI-komponentit

## Sovelluksen toiminnot

### Päänäkymä
- Yläosa (Musta) - Mustan pelaajan aika ja siirrot
- Alaosa (Valkoinen) - Valkoisen pelaajan aika ja siirrot
- Aktiivinen pelaaja - Näkyy vihreällä taustalla
- Tauko - Harmaa tausta, kun peli on keskeytetty

### Ohjausnapit
- Vasen alakulma - RESET (pitkä painallus)
- Oikea alakulma - ASETUKSET (normaali painallus)
- ALOITA PELI - Aloita peli valitulla aikakontrollilla
- JATKA PELIÄ - Jatka tauon jälkeen

### Aikakontrollit

| Nimi | Aika | Increment |
|------|------|-----------|
| Bullet | 1 min | - |
| Blitz | 3 min | - |
| Blitz +2 | 3 min | +2s/siirto |
| Rapid | 5 min | - |
| Rapid +3 | 5 min | +3s/siirto |
| Classical | 10 min | - |
| Classical +5 | 10 min | +5s/siirto |
| Classical +10 | 15 min | +10s/siirto |

## Projektin rakenne

app/src/main/java/com/example/shakkiappi/
- MainActivity.kt          Päänäkymä ja UI
- res/
  - values/                Tekstit ja teemat
  - raw/                   Äänitiedostot

## Asennus

### Debug-versio (kehitys)

git clone https://github.com/Xapeck/shakkiappi.git
cd shakkiappi
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk

### Release-versio (tuotanto)

Luo keystore (vain kerran)
keytool -genkey -v -keystore ~/shakkiappi-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias shakkiappi

Rakenna release APK
./gradlew assembleRelease

Asenna
adb install app/build/outputs/apk/release/app-release.apk

## Käyttöohje

1. Ajan valinta - Paina asetusnappia ja valitse haluamasi aikakontrolli
2. Pelin aloitus - Paina ALOITA PELI
3. Siirron tekeminen - Paina omaa puoliskoasi näytöstä (valkoinen tai musta)
4. Ajan seuraaminen - Näyttö näyttää kuluvan ajan ja siirrot
5. Pelin keskeytys - Paina asetusnappia (aika pysähtyy)
6. Jatkaminen - Paina JATKA PELIÄ
7. Uusi peli - Pidä reset-nappia painettuna pitkään

## Branch-strategia

- main - Tuotantoversio, stabiilit julkaisut
- dev - Kehityshaara, uudet ominaisuudet

## Osallistuminen

1. Forkkaa repositorio
2. Luo uusi feature-haara (git checkout -b feature/ominaisuus)
3. Committaa muutokset (git commit -m 'Lisää uusi ominaisuus')
4. Pushaa haara (git push origin feature/ominaisuus)
5. Avaa Pull Request

## Lisenssi

MIT License

## Tekijät

Xapeck - https://github.com/Xapeck
