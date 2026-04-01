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
- JATKA PELIA - Jatka tauon jälkeen
- Visuaalinen palaute - Aktiivinen pelaaja näkyy vihrealla

## Teknologiat

- Kotlin 1.9.20 - Paaohjelmointikieli
- Jetpack Compose 1.5.4 - Deklaratiivinen UI
- ViewModel 2.7.0 - Tilojen hallinta
- Coroutines 1.7.3 - Ajanlasku ja taustatyot
- Material 3 - Moderni UI-komponentit

## Sovelluksen toiminnot

### Paanakyma
- Ylaosa (Musta) - Mustan pelaajan aika ja siirrot
- Alaosa (Valkoinen) - Valkoisen pelaajan aika ja siirrot
- Aktiivinen pelaaja - Naykyy vihrealla taustalla
- Tauko - Harmaa tausta, kun peli on keskeytetty

### Ohjausnapit
- Vasen alakulma - RESET (pitka painallus)
- Oikea alakulma - ASETUKSET (normaali painallus)
- ALOITA PELI - Aloita peli valitulla aikakontrollilla
- JATKA PELIA - Jatka tauon jalkeen

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
- MainActivity.kt          Paanakyma ja UI
- res/
  - values/                Tekstit ja teemat
  - raw/                   Aanitiedostot

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

## Kayttoohje

1. Ajan valinta - Paina asetusnappia ja valitse haluamasi aikakontrolli
2. Pelin aloitus - Paina ALOITA PELI
3. Siirron tekeminen - Paina omaa puoliskoasi naytosta (valkoinen tai musta)
4. Ajan seuraaminen - Naytto nayttaa kuluvan ajan ja siirrot
5. Pelin keskeytys - Paina asetusnappia (aika pysahtyy)
6. Jatkaminen - Paina JATKA PELIA
7. Uusi peli - Pida reset-nappia painettuna pitkaan

## Branch-strategia

- main - Tuotantoversio, stabiilit julkaisut
- dev - Kehityshaara, uudet ominaisuudet

## Osallistuminen

1. Forkkaa repositorio
2. Luo uusi feature-haara (git checkout -b feature/ominaisuus)
3. Committaa muutokset (git commit -m 'Lisaa uusi ominaisuus')
4. Pushaa haara (git push origin feature/ominaisuus)
5. Avaa Pull Request

## Lisenssi

MIT License

## Tekijat

Xapeck - https://github.com/Xapeck
