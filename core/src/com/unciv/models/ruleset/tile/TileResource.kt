package com.unciv.models.ruleset.tile

import com.unciv.models.ruleset.Ruleset
import com.unciv.models.stats.NamedStats
import com.unciv.models.stats.Stats
import com.unciv.models.translations.tr
import java.util.*

class TileResource : NamedStats() {

    var resourceType: ResourceType = ResourceType.Bonus
    var terrainsCanBeFoundOn: List<String> = listOf()
    var improvement: String? = null
    var improvementStats: Stats? = null

    /**
     * The building that improves this resource, if any. E.G.: Granary for wheat, Stable for cattle.
     */
    var building: String? = null
    var revealedBy: String? = null
    var unique: String? = null


    fun getDescription(ruleset: Ruleset): String {
        val stringBuilder = StringBuilder()
        stringBuilder.appendln(resourceType.name.tr())
        stringBuilder.appendln(this.clone().toString())
        val terrainsCanBeBuiltOnString: ArrayList<String> = arrayListOf()
        terrainsCanBeBuiltOnString.addAll(terrainsCanBeFoundOn.map { it.tr() })
        stringBuilder.appendln("Can be found on ".tr() + terrainsCanBeBuiltOnString.joinToString(", "))
        stringBuilder.appendln()
        stringBuilder.appendln("Improved by [$improvement]".tr())
        stringBuilder.appendln("Bonus stats for improvement: ".tr() + "$improvementStats".tr())

        val buildingsThatConsumeThis = ruleset.buildings.values.filter { it.requiredResource==name }
        if(buildingsThatConsumeThis.isNotEmpty())
            stringBuilder.appendln("Buildings that consume this resource: ".tr()
                    + buildingsThatConsumeThis.joinToString { it.name.tr() })

        val unitsThatConsumeThis = ruleset.units.values.filter { it.requiredResource==name }
        if(unitsThatConsumeThis.isNotEmpty())
            stringBuilder.appendln("Units that consume this resource: ".tr()
                    + unitsThatConsumeThis.joinToString { it.name.tr() })

        if(unique!=null) stringBuilder.appendln(unique!!.tr())
        return stringBuilder.toString()
    }
}

data class ResourceSupply(val resource:TileResource,var amount:Int, val origin:String)

class ResourceSupplyList:ArrayList<ResourceSupply>(){
    fun add(resource: TileResource, amount: Int, origin: String){
        val existingResourceSupply=firstOrNull{it.resource==resource && it.origin==origin}
        if(existingResourceSupply!=null) {
            existingResourceSupply.amount += amount
            if(existingResourceSupply.amount==0) remove(existingResourceSupply)
        }
        else add(ResourceSupply(resource,amount,origin))
    }

    fun add(resourceSupplyList: ResourceSupplyList){
        for(resourceSupply in resourceSupplyList)
            add(resourceSupply.resource,resourceSupply.amount,resourceSupply.origin)
    }
}