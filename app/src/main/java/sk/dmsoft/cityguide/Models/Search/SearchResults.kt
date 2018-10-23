package sk.dmsoft.cityguide.Models.Search

import sk.dmsoft.cityguide.Models.Country
import sk.dmsoft.cityguide.Models.Guides.GuideListItem
import sk.dmsoft.cityguide.Models.Place

/**
 * Created by Daniel on 8. 3. 2018.
 */
class SearchResults {
    var countries: ArrayList<Country> = ArrayList()
    var guides: ArrayList<GuideListItem> = ArrayList()
}