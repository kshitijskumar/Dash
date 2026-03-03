package org.example.dash.data.mapper

import org.example.dash.data.model.DashLink
import org.example.dash.data.model.DashLinksResponse
import org.example.dash.domain.model.DashLinkDomain
import org.example.dash.domain.model.UserDashboard

fun DashLinksResponse.toDomain(): UserDashboard {
    return UserDashboard(
        userId = this.userId,
        links = this.links.map { it.toDomain() }
    )
}

fun DashLink.toDomain(): DashLinkDomain {
    return DashLinkDomain(
        id = this.id,
        name = this.name,
        url = this.url
    )
}
