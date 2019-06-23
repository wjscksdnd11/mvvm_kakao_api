package com.jeon.mvvm_kakao_image.datas



data class KakaoData (val documents:List<ImageData>, val meta: Meta)

data class ImageData(val image_url:String)

data class Meta(val is_end:Boolean)