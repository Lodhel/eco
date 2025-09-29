package com.example.dtl.domain.mappers

import com.example.dtl.data.network.model.OrderResult
import com.example.dtl.domain.model.Rectangle

object RectanglesMapper {
    fun mapOrderResultToRectangle(orderResult: OrderResult) = Rectangle(
        id = orderResult.id,
        label = orderResult.name_plant,
        startX = (orderResult.bbox_norm[0] - orderResult.bbox_norm[2] / 2).toFloat(),
        startY = (orderResult.bbox_norm[1] - orderResult.bbox_norm[3] / 2).toFloat(),
        endX = (orderResult.bbox_norm[0] + orderResult.bbox_norm[2] / 2).toFloat(),
        endY = (orderResult.bbox_norm[1] + orderResult.bbox_norm[3] / 2).toFloat(),
    )
}