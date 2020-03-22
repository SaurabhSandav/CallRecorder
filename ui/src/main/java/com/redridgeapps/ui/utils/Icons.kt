package com.redridgeapps.ui.utils

import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.lazyMaterialIcon
import androidx.ui.material.icons.materialPath

val Icons.Filled.Delete: VectorAsset by lazyMaterialIcon {
    materialPath {
        moveTo(6.0f, 19.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(8.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineTo(7.0f)
        horizontalLineTo(6.0f)
        verticalLineToRelative(12.0f)
        close()
        moveTo(19.0f, 4.0f)
        horizontalLineToRelative(-3.5f)
        lineToRelative(-1.0f, -1.0f)
        horizontalLineToRelative(-5.0f)
        lineToRelative(-1.0f, 1.0f)
        horizontalLineTo(5.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(14.0f)
        verticalLineTo(4.0f)
        close()
    }
}

val Icons.Filled.PlayArrow: VectorAsset by lazyMaterialIcon {
    materialPath {
        moveTo(8.0f, 5.0f)
        verticalLineToRelative(14.0f)
        lineToRelative(11.0f, -7.0f)
        close()
    }
}

val Icons.Filled.Stop: VectorAsset by lazyMaterialIcon {
    materialPath {
        moveTo(6.0f, 6.0f)
        horizontalLineToRelative(12.0f)
        verticalLineToRelative(12.0f)
        horizontalLineTo(6.0f)
        close()
    }
}

val Icons.Filled.ArrowBack: VectorAsset by lazyMaterialIcon {
    materialPath {
        moveTo(20.0f, 11.0f)
        horizontalLineTo(7.83f)
        lineToRelative(5.59f, -5.59f)
        lineTo(12.0f, 4.0f)
        lineToRelative(-8.0f, 8.0f)
        lineToRelative(8.0f, 8.0f)
        lineToRelative(1.41f, -1.41f)
        lineTo(7.83f, 13.0f)
        horizontalLineTo(20.0f)
        verticalLineToRelative(-2.0f)
        close()
    }
}
