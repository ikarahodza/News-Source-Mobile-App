package etf.ri.rma.newsfeedapp.data.network.api

import etf.ri.rma.newsfeedapp.data.network.Data
import etf.ri.rma.newsfeedapp.data.network.Meta

data class NewsApiResponse(
    val `data`: List<Data>,
    val meta: Meta
)
