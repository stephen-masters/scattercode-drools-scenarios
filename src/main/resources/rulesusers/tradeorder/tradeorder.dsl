
[when][]For each tick = $t : Tick() over window:length (1);

[when][]Given a trade order =  $tradeOrder : TradeOrder() 

[when][]- older than "{age}" = orderOpenTime before [{age}] $t.tickTime

[then][]Close trade = $tradeOrder.setStatus( "CLOSING" ); update( $tradeOrder );




//  The rule we're trying to support.
//
//  rule "close old trades"
//    dialect "mvel"
//  when
//    t : Tick( ) over window:length (1)
//    closeMe : TradeOrder( orderOpenTime before [2m] t.tickTime )
//  then
//    closeMe.setStatus( OrderStatus.CLOSING );
//    update( closeMe );
//    modifiedOrderList.add(closeMe);
//  end

//  A simplified rule for the purposes of this example.
//
//  rule "close old trades"
//    dialect "mvel"
//  when
//    $t : Tick( ) over window:length (1)
//    $tradeOrder : TradeOrder( orderOpenTime before [2m] $t.tickTime )
//  then
//    // Changed status to String to avoid creating enum for example.
//    $tradeOrder.setStatus( "CLOSING" );
//    update( $tradeOrder );
//    // Commented out to avoid setting up global for example. 
//    // modifiedOrderList.add($tradeOrder);
//  end

