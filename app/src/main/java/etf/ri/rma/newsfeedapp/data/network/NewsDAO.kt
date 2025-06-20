package etf.ri.rma.newsfeedapp.data.network
import android.util.Log
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidUUIDException
import etf.ri.rma.newsfeedapp.data.network.retrofit.RetrofitInstanceNews
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class NewsDAO {
    private var apiService = RetrofitInstanceNews.api
    private val API_TOKEN = "Nihq8wE92WRug2VxGoWIjmc2huWzgzUmBEnecIRO"

    private val allStoriesCache = mutableListOf<Data>()
    private var initialDataLoaded = false
    private val categoryTimestamps = mutableMapOf<String, Long>()
    private val categoryCache = mutableMapOf<String, List<Data>>()
    private val similarStoriesCache = mutableMapOf<String, List<Data>>()

    private val initialNewsData = listOf(
        Data(
            uuid = "017a3842-99a8-426a-a898-4fe2c31ca297",
            title = "Wayne Gretzky Explains Barefoot Golf Photo With Travis Kelce",
            description = "Wayne Gretzky explains the viral barefoot golf photo with Travis Kelce during an appearance on the 'New Heights' podcast",
            keywords = "",
            snippet = "Wayne Gretzky has finally explained what he was doing barefoot on the golf course with Travis Kelce in a photo that went viral last year.\n\nGretzky, 64, appeared...",
            url = "https://www.usmagazine.com/entertainment/news/wayne-gretzky-explains-barefoot-golf-photo-with-travis-kelce/",
            image_url = "https://www.usmagazine.com/wp-content/uploads/2025/05/GettyImages-2186902037Wayne-Gretzky-Finally-Explains-Barefoot-Golf-Photo-With-Travis-Kelce.jpg?w=1200&h=630&crop=1&quality=86&strip=all",
            language = "en",
            published_at = "2025-05-28T18:46:34.000000Z",
            source = "usmagazine.com",
            categories = listOf("entertainment", "general"),
            locale = "us"
        ),
        Data(
            uuid = "273dc573-3f8d-4f15-a6dd-ff0ddf56bb79",
            title = "Judge takes step toward possible release of detained Russian Harvard scientist accused of smuggling",
            description = "A federal judge ruled in favor of Russian Harvard scientist Kseniia Petrova, detained by ICE for smuggling charges, ahead of her criminal bail hearing.",
            keywords = "",
            snippet = "A federal judge in Vermont ruled in favor of a Russian Harvard scientist who was detained by ICE and accused of smuggling goods into the U.S. on Wednesday.\n\nKse...",
            url = "https://www.foxnews.com/politics/judge-takes-step-toward-possible-release-detained-russian-harvard-scientist-accused-smuggling",
            image_url = "https://static.foxnews.com/foxnews.com/content/uploads/2025/04/kseniia-petrova3.jpg",
            language = "en",
            published_at = "2025-05-28T18:46:10.000000Z",
            source = "foxnews.com",
            categories = listOf("general", "politics"),
            locale = "us"
        ),
        Data(
            uuid = "4246cbcd-f7d4-4e43-9ba0-36d34824ebe6",
            title = "White Lotus Season 4: What to Know About Locations, Star-Studded Cast",
            description = "'The White Lotus' was renewed for a fourth season, which will be set in a new location and with new cast members",
            keywords = "",
            snippet = "Most details about season 4 of The White Lotus are under wraps but there have been hints about a possible location and potential stars.\n\nThe HBO series, which d...",
            url = "https://www.usmagazine.com/entertainment/news/white-lotus-season-4-what-to-know-about-locations-star-studded-cast/",
            image_url = "https://www.usmagazine.com/wp-content/uploads/2025/05/White-Lotus-Season-4.jpg?w=1200&h=630&crop=1&quality=86&strip=all",
            language = "en",
            published_at = "2025-05-28T18:45:55.000000Z",
            source = "usmagazine.com",
            categories = listOf("entertainment", "general"),
            locale = "us"
        ),
        Data(
            uuid = "8f9ccc4c-6a1b-4fb8-9d4f-a0bb9ac4c3a6",
            title = "Senate Republican campaign committee 'grills' Chuck Schumer on National Hamburger Day",
            description = "Senate Republican campaign arm mocks Chuck Schumer's grilling skills in a new video, urging voters to join the GOP. Democrats face challenges with male voters i...",
            keywords = "",
            snippet = "The National Republican Senatorial Committee is revisiting a much-maligned year-old photo from Senate Democratic Leader Chuck Schumer.\n\nThe NRSC, which is the c...",
            url = "https://www.foxnews.com/politics/senate-republican-campaign-committee-grills-chuck-schumer-national-hamburger-day",
            image_url = "https://static.foxnews.com/foxnews.com/content/uploads/2025/05/chuck-schumer.jpg",
            language = "en",
            published_at = "2025-05-28T18:42:40.000000Z",
            source = "foxnews.com",
            categories = listOf("general", "politics"),
            locale = "us"
        ),
        Data(
            uuid = "5c295d22-aeab-4a89-b80e-fedadd5aa113",
            title = "An Abbott competitor drops out — plus, a special circumstance for Nvidia earnings",
            description = "Every weekday, the Investing Club releases the Homestretch; an actionable afternoon update just in time for the last hour of trading.",
            keywords = "Wells Fargo & Co, Abbott Laboratories, Edwards Lifesciences Corp, Medtronic PLC, Boston Scientific Corp, S&P 500 Index, Salesforce Inc, NVIDIA Corp, Jim Cramer, Investment strategy, Markets, Breaking News: Markets, Homestretch, business news",
            snippet = "Every weekday, the CNBC Investing Club with Jim Cramer releases the Homestretch — an actionable afternoon update, just in time for the last hour of trading on...",
            url = "https://www.cnbc.com/2025/05/28/abbott-competitor-drops-out-special-circumstance-for-nvidia-earnings.html",
            image_url = "https://image.cnbcfm.com/api/v1/image/108129613-1744233967674-gettyimages-2208760921-US_STOCKS.jpeg?v=1744234108&w=1920&h=1080",
            language = "en",
            published_at = "2025-05-28T18:41:49.000000Z",
            source = "cnbc.com",
            categories = listOf("general", "business"),
            locale = "us"
        ),
        Data(
            uuid = "96eb92cd-19de-42f3-8f34-65ef29a8b95c",
            title = "Why Costco is thriving while Target and Walgreens are struggling",
            description = "Costco is succeeding thanks to its membership model and consumer trust, in contrast to struggles faced by Target and Walgreens amid changing retail dynamics.",
            keywords = "",
            snippet = "As major retailers struggle with changing consumer behavior, one stands out for its consistency: Costco.\n\nThe warehouse giant, famous for its low prices, bulk o...",
            url = "https://www.cnn.com/2025/05/28/business/costco-target-walgreens-retail/index.html",
            image_url = "https://media.cnn.com/api/v1/images/stellar/prod/230221104432-costco-storefront-012023.jpg?c=16x9&q=w_800,c_fill",
            language = "en",
            published_at = "2025-05-28T18:38:12.000000Z",
            source = "cnn.com",
            categories = listOf("general", "business"),
            locale = "us"
        ),
        Data(
            uuid = "a15e5985-9de2-42b6-b90b-9ec648b5e4f4",
            title = "UFC fighter’s dad claims he was sucker punched by opponent’s team member in wild post-fight melee",
            description = "A post-fight brawl at UFC Vegas 91 involved Igor Severino's father and Andre Lima's cornerman. Security quickly stepped in.",
            keywords = "",
            snippet = "A wild post-fight melee broke out at UFC Vegas 91 after a bout between Igor Severino and Andre Lima.\n\nSeverino’s father claimed he was sucker punched by a mem...",
            url = "https://nypost.com/2025/05/28/sports/ufc-fighters-dad-claims-he-was-sucker-punched-by-opponents-team-member-in-wild-post-fight-melee/",
            image_url = "https://nypost.com/wp-content/uploads/sites/2/2025/05/NYPICHPDPICT000055781770.jpg?quality=75&strip=all",
            language = "en",
            published_at = "2025-05-28T18:37:47.000000Z",
            source = "nypost.com",
            categories = listOf("sports", "general"),
            locale = "us"
        ),
        Data(
            uuid = "df9fe6b5-05e6-4530-9709-54bcaefea325",
            title = "Dolphins were right about Tua Tagovailoa, wrong about everyone else in 2020",
            description = "An analysis of the Miami Dolphins’ 2020 NFL draft class reveals mixed results, with Tua Tagovailoa proving to be a bright spot amid other missteps.",
            keywords = "",
            snippet = "The Miami Dolphins made several bold decisions during the 2020 NFL Draft.\n\nWhile their pick of quarterback Tua Tagovailoa has paid off, other choices from that...",
            url = "https://sports.yahoo.com/dolphins-were-right-about-tua-tagovailoa-wrong-about-everyone-else-in-2020-183737991.html",
            image_url = "https://s.yimg.com/ny/api/res/1.2/7Z6CzNHzS1N3fSzCKaaZtA--/YXBwaWQ9aGlnaGxhbmRlcjt3PTEyMDA7aD03ODk-/https://media.zenfs.com/en/yahoo_sports_articles_546/ab4ae3a627e7181dcb1a0db5fef1c15e",
            language = "en",
            published_at = "2025-05-28T18:37:37.000000Z",
            source = "yahoo.com",
            categories = listOf("sports", "general"),
            locale = "us"
        ),
        Data(
            uuid = "de625f4e-bddb-4f3c-878b-cfa7a9ed5d8f",
            title = "Andreessen Horowitz makes new bet on crypto amid market comeback",
            description = "Venture capital firm Andreessen Horowitz is launching a new $600 million crypto fund, showing renewed interest in the sector.",
            keywords = "",
            snippet = "Venture capital giant Andreessen Horowitz is betting big on crypto again.\n\nThe firm announced Wednesday that it is launching a new $600 million fund focused on...",
            url = "https://www.theverge.com/2025/5/28/24164020/andreessen-horowitz-crypto-fund-investment",
            image_url = "https://cdn.vox-cdn.com/thumbor/f-lXcsDqS6uE1AHPmcPtFSLB9xI=/0x0:2040x1360/920x613/filters:focal(1020x680:1021x681)/cdn.vox-cdn.com/uploads/chorus_asset/file/23993295/STK093_Google_05.jpg",
            language = "en",
            published_at = "2025-05-28T18:35:45.000000Z",
            source = "theverge.com",
            categories = listOf("technology", "business"),
            locale = "us"
        ),
        Data(
            uuid = "70a39e34-8c8f-4c3d-a91d-00e57f66ae63",
            title = "Justice Alito declines to recuse himself in Jan. 6-related cases",
            description = "Supreme Court Justice Samuel Alito has declined to recuse himself from cases related to the January 6 Capitol riot despite scrutiny over his impartiality.",
            keywords = "",
            snippet = "Supreme Court Justice Samuel Alito said Wednesday he will not recuse himself from cases related to the January 6, 2021, Capitol riot.\n\nAlito has faced calls fro...",
            url = "https://www.nbcnews.com/politics/supreme-court/justice-alito-declines-recuse-jan-6-related-cases-rcna154645",
            image_url = "https://media-cldnry.s-nbcnews.com/image/upload/t_fit-760w,f_auto,q_auto:best/rockcms/2025-05/240515-samuel-alito-ew-334p-5e3651.jpg",
            language = "en",
            published_at = "2025-05-28T18:35:38.000000Z",
            source = "nbcnews.com",
            categories = listOf("politics", "general"),
            locale = "us"
        )
    )

    fun setApiService(service: NewsApiService) {
        this.apiService = service
    }

    public fun ensureInitialDataLoaded() {
        if (!initialDataLoaded) {
            //allStoriesCache.clear()
            allStoriesCache.addAll(initialNewsData)
            initialDataLoaded = true
        }
    }

    suspend fun getAllStories(): List<Data> = withContext(Dispatchers.IO) {
        //ensureInitialDataLoaded()
        return@withContext allStoriesCache
    }

    suspend fun getTopStoriesByCategory(category: String): List<Data> = withContext(Dispatchers.IO) {
        ensureInitialDataLoaded()
        val currentTime = System.currentTimeMillis()
        val lastFetchTime = categoryTimestamps[category] ?: 0L
        val isCacheValid = (currentTime - lastFetchTime) < 30_000

        if (isCacheValid && categoryCache.containsKey(category)) {
            return@withContext categoryCache[category]!!.take(3)
        }

        try {
            val response = apiService.getTopStoriesByCategory(
                category = category,
                limit = 3,
                apiToken = API_TOKEN,
                keywords = null,
                language = "en",
                locale = "us",
                relevance_score = null,
                snippet = true
            )
            val stories = response.data
            categoryCache[category] = stories
            categoryTimestamps[category] = currentTime

            for (story in stories) {
                if (allStoriesCache.none { it.uuid == story.uuid }) {
                    allStoriesCache.add(0, story)
                }
            }

            return@withContext stories
        } catch (e: Exception) {
            if (categoryCache.containsKey(category)) {
                return@withContext categoryCache[category]!!.take(3)
            }
            return@withContext allStoriesCache.filter {
                it.categories.contains(category)
            }.take(3)
        }
    }


   /* suspend fun getSimilarStories(uuid: String): List<Data> = withContext(Dispatchers.IO) {
        if (!isValidUUID(uuid)) {
            throw InvalidUUIDException("Invalid UUID: $uuid")
        }

        if (similarStoriesCache.containsKey(uuid)) {
            Log.d("NewsDAO", "Nađen u kešu za $uuid")
            return@withContext similarStoriesCache[uuid]!!
        }

        similarStoriesCache[uuid]?.let { return@withContext it }

        return@withContext try {
            val response = apiService.getSimilarStories(
                uuid = uuid,
                limit = 2,
                apiToken = API_TOKEN
            )
            val similarStories = response.data
            Log.d("NewsDAO", "API vratio ${similarStories.size} vijesti")


            similarStoriesCache[uuid] = similarStories
            return@withContext similarStories
        } catch (e: Exception) {
            Log.e("NewsDAO", "Greška prilikom poziva API-ja: ${e.message}", e)

            val story = allStoriesCache.find { it.uuid == uuid }
            if (story != null) {
                val category = story.categories.firstOrNull() ?: "general"
                return@withContext allStoriesCache.filter {
                    it.uuid != uuid && it.categories.contains(category)
                }.take(2)
            }
            return@withContext emptyList()
        }
    }

    */

    suspend fun getSimilarStories(uuid: String): List<Data> = withContext(Dispatchers.IO) {
        if (!isValidUUID(uuid)) {
            throw InvalidUUIDException("Invalid UUID: $uuid")
        }

        if (similarStoriesCache.containsKey(uuid)) {
            Log.d("NewsDAO", "Nađen u kešu za $uuid")
            return@withContext similarStoriesCache[uuid]!!
        }

        return@withContext try {
            val response = apiService.getSimilarStories(
                uuid = uuid,
                limit = 2,
                apiToken = API_TOKEN
            )
            val similarStories = response.data
            Log.d("NewsDAO", "API vratio ${similarStories.size} vijesti")

            similarStoriesCache[uuid] = similarStories
            similarStories
        } catch (e: Exception) {
            Log.e("NewsDAO", "Greška prilikom poziva API-ja: ${e.message}", e)

            val story = allStoriesCache.find { it.uuid == uuid }
            if (story != null) {
                val category = story.categories.firstOrNull() ?: "general"
                allStoriesCache.filter {
                    it.uuid != uuid && it.categories.contains(category)
                }.take(2)
            } else {
                emptyList()
            }
        }
    }

    private fun isValidUUID(uuidString: String): Boolean {
        val pattern = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
        )
        return pattern.matcher(uuidString).matches()
    }
}