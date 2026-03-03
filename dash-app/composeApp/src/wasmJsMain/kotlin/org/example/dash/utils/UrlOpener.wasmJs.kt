package org.example.dash.utils

import kotlinx.browser.window

class WasmJsUrlOpener : UrlOpener {
    override fun openUrl(url: String) {
        window.open(url, "_blank")
    }
}

actual fun getUrlOpener(): UrlOpener = WasmJsUrlOpener()
