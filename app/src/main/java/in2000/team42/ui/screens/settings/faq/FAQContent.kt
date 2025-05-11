package in2000.team42.ui.screens.settings.faq

import in2000.team42.R

data class FAQ(
    val questionResId: Int,
    val answerResId: Int
)

val faqItems = listOf(
    FAQ(R.string.faq_1_question, R.string.faq_1_answer),
    FAQ(R.string.faq_2_question, R.string.faq_2_answer),
    FAQ(R.string.faq_3_question, R.string.faq_3_answer),
    FAQ(R.string.faq_4_question, R.string.faq_4_answer),
    FAQ(R.string.faq_5_question, R.string.faq_5_answer)
)