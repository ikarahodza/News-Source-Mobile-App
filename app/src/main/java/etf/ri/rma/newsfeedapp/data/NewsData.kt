package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.model.NewsItem

object NewsData {
    fun getAllNews(): List<NewsItem> {
        return listOf(
            NewsItem(
                id = "1",
                title = "Britanski premijer: Stanje daleko od idealnog, “stvari pogoršati prije nego što postanu bolje”",
                snippet = "Keir Starmer izjavio je u utorak da će biti potrebno mnogo vremena da se Britanija obnovi i oslobodi svih nedaća koje su, kako kaže, zavladale kraljevstvom pod prethodnom konzervativnom vladom...",
                imageUrl = null,
                category = "Politika",
                isFeatured = false,
                source = "Klix",
                publishedDate = "2024-09-27"
            ),
            NewsItem(
                id = "2",
                title = "Objavljen sastav Real Madrida za novu sezonu s Alonsom na klupi: Nema Modrića i još tri zvijezde!",
                snippet = "Unazad sedmicama se šire glasine da će Xabi Alonso postati novi trener Real Madrida, a sad se pojavio i mogući sastav Kraljeva za narednu takmičarsku godinu i to bez niza velikih zvijezda.",
                imageUrl = null,
                category = "Sport",
                isFeatured = true,
                source = "SportSport",
                publishedDate = "2025-04-02"
            ),
            NewsItem(
                id = "3",
                title = "Arktik je u nevolji, cijeli svijet će osjetiti posljedice",
                snippet = "Arktik je dugo ispunjavao ljude strahopoštovanjem, ali sada postoje duboko zabrinjavajući signali koji dolaze iz zaleđenog krajolika na vrhu našeg planeta, a naučnici su duboko zabrinuti za njegovu budućnost jer Trumpova administracija povlači SAD iz globalne klimatske strategije i uništava svoje naučne agencije.",
                imageUrl = null,
                category = "Nauka/tehnologija",
                isFeatured = false,
                source = "N1",
                publishedDate = "2025-03-12"
            ),
            NewsItem(
                id = "4",
                title = "Članovi Predsjedništva BiH održali sastanak s Erdoganom u Antaliji",
                snippet = "Iako detalji razgovora nisu zvanično saopćeni, pretpostavlja se da je jedna od tema bila aktuelna politička kriza u Bosni i Hercegovini...",
                imageUrl = null,
                category = "Politika",
                isFeatured = true,
                source = "Klix",
                publishedDate = "2025-04-02"
            ),
            NewsItem(
                id = "5",
                title = "Elvedina Muzaferija osvojila osmo mjesto u superveleslalomu na Državnom prvenstvu Francuske",
                snippet = "Greška u četvrtom sektoru udaljila ju je od podijuma te je s vremenom 1:27.90 zauzela osmo mjesto, nakon što je prethodno na utrci spusta bila drugoplasirana.",
                imageUrl = null,
                category = "Sport",
                isFeatured = true,
                source = "Sportski žurnal",
                publishedDate = "2025-04-04"
            ),
            NewsItem(
                id = "6",
                title = "EU će graditi nekoliko ‘megafabrika’ vještačke inteligencije",
                snippet = "Dok fabrike vještačke inteligencije sa najboljim učinkom imaju superkompjutere opremljene sa do 25.000 naprednih AI procesora, megafabrika bi premašila 100.000 procesora AI.",
                imageUrl = null,
                category = "Nauka/tehnologija",
                isFeatured = true,
                source = "Al-jazeera",
                publishedDate = "2025-04-10"
            ),
            NewsItem(
                id = "7",
                title = "UN: Trgovinski rat ‘katastrofalan’ za zemlje u razvoju",
                snippet = "Svjetska tržišta u petak su i dalje suočena s turbulencijama iako je Trump objavio da na 90 dana odgađa uvođenje carina svima osim Kini.",
                imageUrl = null,
                category = "Politika",
                isFeatured = true,
                source = "Al-jazeera",
                publishedDate = "2025-04-10"
            ),
            NewsItem(
                id = "8",
                title = "U Sarajevu počeo Turnir mladih džudo nada, stigli i osvajači olimpijskih medalja",
                snippet = "Organizacijom Turnira mladih nada, koji je počeo danas u Sarajevo, i Kodokan seminara, koji je startao u petak, Džudo savez Bosne i Hercegovine zaokružio je jednogodišnji ciklus takmičenja..",
                imageUrl = null,
                category = "Sport",
                isFeatured = false,
                source = "Klix",
                publishedDate = "2025-04-12"
            ),
            NewsItem(
                id = "9",
                title = "Naučnici na tragu uzroka tajanstvenog pada broja ptica u amazonskoj džungli",
                snippet = "Duboko u srcu ekvadorske Amazonije, u jednom od najizolovanijih biodiverzitetskih centara na svijetu, nešto se neobjašnjivo događa s pticama. ..",
                imageUrl = null,
                category = "Nauka/tehnologija",
                isFeatured = false,
                source = "N1",
                publishedDate = "2025-01-30"
            ),
            NewsItem(
                id = "10",
                title = "Američki dolar pao na najnižu vrijednost u posljednje tri godine, strahuje se od recesije",
                snippet = "Indeks američkog dolara pao je u petak na 3-godišnju najnižu cifru od 99,014 jer je agresivna trgovinska politika američkog predsjednika Donalda Trumpa ojačala zabrinutost zbog recesije u zemlji.",
                imageUrl = null,
                category = "Politika",
                isFeatured = false,
                source = "Klix",
                publishedDate = "2025-04-12"
            ),
            NewsItem(
                id = "11",
                title = "Analiza protivnika: Šta čeka Bosnu i Hercegovinu na Eurobasketu?",
                snippet = "Košarkaška reprezentacija Bosne i Hercegovine saznala je protivnike grupne faze Eurobasketa, a naš nacionalni tim svoje utakmice igrat će na Kipru (Limassol).",
                imageUrl = null,
                category = "Sport",
                isFeatured = true,
                source = "N1",
                publishedDate = "2025-03-28"
            ),
            NewsItem(
                id = "12",
                title = "Misterija Saturnovog mjeseca sve dublja",
                snippet = "NASA je objavila da su najnoviji podaci iz arhive misije Cassini otkrili jake indikacije o postojanju tečnog okeana ispod ledene kore Saturnovog mjeseca Enceladusa.",
                imageUrl = null,
                category = "Nauka/tehnologija",
                isFeatured = false,
                source = "Raport",
                publishedDate = "2025-04-12"
            ),
            NewsItem(
                id = "13",
                title = "Dr. Nedim Hogić: Kako podložnost BiH stranom utjecaju doprinosi korupciji",
                snippet = "Postoji niz faktora koji doprinose visokom stepenu korupcije u Bosni i Hercegovini, a jedan od glavnih je \"snažna i neuobičajena podložnost zemlje stranom utjecaju i lobiranju\", objašnjava dr. Nedim Hogić...",
                imageUrl = null,
                category = "Politika",
                isFeatured = true,
                source = "N1",
                publishedDate = "2025-04-11"
            ),
            NewsItem(
                id = "14",
                title = "Bayern traži napadača, prvi izbor golgeter porijekom iz BiH",
                snippet = "Benjamin Šeško, Viktor Gyokeres i Moise Kean nalaze se na užem izboru Bayern Munchena za novog napadača, prenosi ESPN...",
                imageUrl = null,
                category = "Sport",
                isFeatured = false,
                source = "Raport",
                publishedDate = "2025-04-02"
            ),
            NewsItem(
                id = "15",
                title = "Naučnici oživjeli izumrlu vrstu vuka koja je postala poznata zahvaljujući \"Game of Thronesu\"",
                snippet = "Američka kompanija Colossal Biosciences, specijalizovana za genetski inžinjering, objavila je da su njeni naučnici uspjeli da uzgoje tri šteneta vuka iz vrste koja je izumrla prije više od 10.000 godina,...",
                imageUrl = null,
                category = "Nauka/tehnologija",
                isFeatured = true,
                source = "Klix",
                publishedDate = "2025-04-8"
            ),
            NewsItem(
                id = "16",
                title = "Helez se sastao s turskim ministrom odbrane: Nakon oklopnih vozila i bespilotnih letjelica, šta je sada na redu?",
                snippet = "Zamjenik predsjedavajuće Vijeća ministara i ministar odbrane Bosne i Hercegovine Zukan Helez, na marginama 4. Diplomatskog foruma u Antaliji sastao se sa turskim kolegom Yasarom Gulerom...",
                imageUrl = null,
                category = "Politika",
                isFeatured = false,
                source = "Raport",
                publishedDate = "2025-04-12"
            ),
            NewsItem(
                id = "17",
                title = "(FOTO) Grbavica spremna za meč Željezničara i GOŠK-a",
                snippet = "Igrači obje ekipe imat će idealne uslove za igru, s obzirom na to da je teren stadiona Grbavica u dobroj stanju...",
                imageUrl = null,
                category = "Sport",
                isFeatured = true,
                source = "Oslobodjenje",
                publishedDate = "2025-04-02"
            ),
            NewsItem(
                id = "18",
                title = "Adi Pandžić o potencijalu 3D printanja: Od igara do spašavanja života",
                snippet = "Za docenta Mašinskog fakulteta u Sarajevu, dr. Adija Pandžića, 3D printanje je moćan alat koji mijenja industriju i medicinu.",
                imageUrl = null,
                category = "Nauka/tehnologija",
                isFeatured = true,
                source = "Oslobodjenje",
                publishedDate = "2025-04-10"
            ),
            NewsItem(
                id = "19",
                title = "UN najavio smanjenje broja humanitarnih radnika za 20 posto zbog smanjenja finansiranja",
                snippet = "Neki od rezova došli su iz SAD-a. Neki su došli iz drugih zemalja, kaže glasnogovornik UN-a.",
                imageUrl = null,
                category = "Politika",
                isFeatured = true,
                source = "Al-jazeera",
                publishedDate = "2025-04-07"
            ),
            NewsItem(
                id = "20",
                title = "Stopirani pregovori Flicka i Barcelone, otkriveno kakav uslov je postavio za produženje ugovora",
                snippet = "Čelnici Barcelone odlučili su ponuditi Hansiju Flicku novi ugovor, ali njemački stručnjak im je rekao šta mu je jasan uslov koji moraju ispuniti ukoliko žele da potpiše produženje saradnje.",
                imageUrl = null,
                category = "Sport",
                isFeatured = false,
                source = "SportSport",
                publishedDate = "2025-04-12"
            )
        )
    }
}