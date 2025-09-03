//package zw.co.afrosoft.zdf.feign.clients;
//
//import feign.Headers;
//import feign.RequestLine;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.RequestBody;
//import zw.co.afrosoft.zdf.feign.configs.RentalFeignClientConfig;
//
//@FeignClient(name = "rental-invoice-service", configuration = RentalFeignClientConfig.class)
//public interface RentalInvoiceClient {
//
//    @RequestLine("POST /rental-invoice")
//    @Headers("Content-Type: application/json")
//    Invoice createInvoice(@RequestBody InvoiceRequest request);
//}
