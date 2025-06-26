package org.everbuild.celestia.orion.core.luckperms

interface LuckPermsData {
    val permissionsPrefix: String
    val permissionsColor: String
    val permissionsDisplayName: String
    val permissionsWeight: Int
    val permissionsGroupNameRaw: String
    val playerName: String
    val isChatImportant: Boolean
}