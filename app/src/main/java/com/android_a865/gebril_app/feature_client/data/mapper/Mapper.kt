package com.android_a865.gebril_app.feature_client.data.mapper

import com.android_a865.gebril_app.feature_client.data.entities.ClientEntity
import com.android_a865.gebril_app.feature_client.domain.model.Client

fun ClientEntity.toClient(): Client {
    return Client(
        id = id,
        name = name,
        org = org,
        title = title,
        phone1 = phone1,
        phone2 = phone2,
        email = email,
        address = address
    )
}


fun Client.toEntity(): ClientEntity {
    return ClientEntity(
        id = id,
        name = name,
        org = org,
        title = title,
        phone1 = phone1,
        phone2 = phone2,
        email = email,
        address = address
    )
}

