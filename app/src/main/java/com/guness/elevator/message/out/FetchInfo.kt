package com.guness.elevator.message.out

import com.guness.elevator.message.AbstractMessage
import com.guness.elevator.model.Fetch

/**
 * Created by guness on 24.12.2017.
 */
class FetchInfo() : AbstractMessage() {
    var fetch: Fetch? = null

    constructor(fetch: Fetch) : this() {
        this.fetch = fetch
    }
}