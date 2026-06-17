package com.example.otnet.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PublisherSettings(
    val ageRatings: AgeRatingsConfig? = null,
    val pinProtection: ToggleConfig? = null,
    val profileLimit: ProfileLimitConfig? = null,
    val epg: EpgConfig? = null,
    val errorReporting: ToggleConfig? = null,
    val sessionDrm: ToggleConfig? = null,
    val myList: MyListConfig? = null,
    val adPolicy: AdPolicy? = null,
    val branding: Branding? = null,
    val debugOverlay: ToggleConfig? = null,
    val viewerAuth: ViewerAuthConfig? = null,
)

@Serializable
data class ToggleConfig(val enabled: Boolean? = null)

@Serializable
data class AgeRatingsConfig(
    val enabled: Boolean? = null,
    val ratingSystem: String? = null,
    val ratings: List<String> = emptyList(),
)

@Serializable
data class ProfileLimitConfig(
    val enabled: Boolean? = null,
    val max: Int? = null,
)

@Serializable
data class EpgConfig(
    val enabled: Boolean? = null,
    val futureHours: Int? = null,
    val pastMinutes: Int? = null,
)

@Serializable
data class MyListConfig(
    val enabled: Boolean? = null,
    val showOnHomepage: Boolean? = null,
    val showInNav: Boolean? = null,
)

@Serializable
data class AdPolicy(val blockSkipping: Boolean? = null)

@Serializable
data class Branding(
    val name: String? = null,
    val logo: String? = null,
)

@Serializable
data class ViewerAuthConfig(
    val mode: String? = null,
    val externalUrl: String? = null,
    val plans: List<Plan> = emptyList(),
)

@Serializable
data class Plan(
    val name: String? = null,
    val stripePriceId: String? = null,
    val amount: Int? = null,
    val currency: String? = null,
    val interval: String? = null,
)

val PublisherSettings.epgEnabled: Boolean get() = epg?.enabled ?: true
val PublisherSettings.myListEnabled: Boolean get() = myList?.enabled ?: true
val PublisherSettings.myListShowInNav: Boolean get() = myList?.showInNav ?: true
val PublisherSettings.brandName: String
    get() = branding?.name?.takeIf { it.isNotBlank() } ?: "OTNet"
val PublisherSettings.blockAdSkipping: Boolean get() = adPolicy?.blockSkipping ?: false
val PublisherSettings.ageRatingsEnabled: Boolean get() = ageRatings?.enabled ?: false
