# weixin-pay-test
因为在单元测试环境中是不太可能真的创建一个微信支付的订单的，
所以伪造一个 WeixinPaymentForm 让它返回尽可能接近真实的数据，同时整合 payment-test 期望获取更好的效果。