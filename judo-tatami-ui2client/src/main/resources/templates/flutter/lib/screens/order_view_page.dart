import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:judo/components/judo_button.dart';
import 'package:judo/screens/order_edit_page.dart';
import 'package:judo/store/internal/northwind_internal_ap_store.dart';
import 'package:provider/provider.dart';

class OrderViewPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final internalApStore = Provider.of<NorthwindInternalApStore>(context);

    return SafeArea(
      child: Scaffold(
        body: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Row(
                children: [
                  JudoButton(
                    col: 1,
                    label: 'Back',
                    icon: Icon(Icons.arrow_back_ios),
                    onPressed: () => Navigator.pop(context),
                  ),
                  JudoButton(
                    col: 1,
                    label: 'Edit',
                    icon: Icon(Icons.edit),
                    onPressed: () => Navigator.push(
                        context,
                        MaterialPageRoute(
                            builder: (context) => OrderEditPage())),
                  ),
                ],
              ),
              Row(
                children: [
                  Expanded(
                      child: Observer(
                    builder: (_) => Text(
                        'Description: ${internalApStore.currentOrderInfo.customsDescription}'),
                  )),
                  Expanded(
                      child: Observer(
                    builder: (_) => Text(
                        'Excise Tax: ${internalApStore.currentOrderInfo.exciseTax}'),
                  )),
                  Expanded(
                      child: Observer(
                    builder: (_) => Text(
                        'Shipper: ${internalApStore.currentOrderInfo.shipper.companyName}'),
                  )),
                ],
              ),
              Row(
                children: [
                  Expanded(
                    child: Container(
                      height: 500,
                      child: Observer(
                          builder: (_) => ListView.builder(
                              itemCount:
                                  internalApStore.currentOrderInfo.items.length,
                              itemBuilder: (BuildContext ctxt, int index) {
                                return Row(
                                  children: [
                                    Expanded(
                                      child: Text(internalApStore
                                          .currentOrderInfo
                                          .items[index]
                                          .product
                                          .productName),
                                    ),
                                    Expanded(
                                      child: Text(internalApStore
                                          .currentOrderInfo
                                          .items[index]
                                          .quantity
                                          .toString()),
                                    ),
                                  ],
                                );
                              })),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
