import 'package:judo/rest/internal_ap/lib/api.dart';
import 'package:judo/store/internal_ap/northwind_internal_ap_shipper_info_store.dart';
import 'package:judo/utilities/constants.dart';
import 'package:openapi_dart_common/openapi.dart';

class NorthwindInternalShipperInfoRepository {
  final ApiClient _apiClient =
      ApiClient(basePath: kBasePathUrl, apiClientDelegate: DioClientDelegate());

  Future createShipper(NorthwindInternalShipperInfoStore shipper) async {
    var shipperInfoExtended = NorthwindServicesShipperInfoExtended();

    shipperInfoExtended.companyName = shipper.companyName;

    NorthwindServicesShipperInfo shipperInfo = await DefaultApi(_apiClient)
        .northwindInternalAPCreateAllShippers(shipperInfoExtended);

    shipper.identifier = shipperInfo.identifier;
    shipper.companyName = shipperInfo.companyName;
  }

  Future removeShipper(NorthwindInternalShipperInfoStore shipperInfo) async {}

  Future updateShipper() async {}

  Future<NorthwindInternalShipperInfoStore> getShipper() async {}

  Future getAll(List<NorthwindInternalShipperInfoStore> shipperInfoList) async {
    List<NorthwindServicesShipperInfo> list =
        await DefaultApi(_apiClient).northwindInternalAPGetAllShippers();

    List<NorthwindInternalShipperInfoStore> tempShipperList = [];

    list.forEach((element) async {
      if (!shipperInfoList
          .any((shipper) => shipper.identifier == element.identifier)) {
        NorthwindInternalShipperInfoStore newShipper =
            NorthwindInternalShipperInfoStore();
        newShipper.identifier = element.identifier;
        newShipper.companyName = element.companyName;
        tempShipperList.add(newShipper);
      }
    });

    shipperInfoList.addAll(tempShipperList);
  }
}
