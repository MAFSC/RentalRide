package com.rentalride.api.contract;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple8;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/LFDT-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.8.0.
 */
@SuppressWarnings("rawtypes")
@Generated("org.web3j.codegen.SolidityFunctionWrapperGenerator")
public class CarBridge extends Contract {
    public static final String BINARY = "6080346100c557601f61158e38819003918201601f19168301916001600160401b038311848410176100c9578084926020946040528339810103126100c557516001600160a01b038116908190036100c55780156100805760018060a01b031990815f5416175f55339060015416176001556040516114b090816100de8239f35b60405162461bcd60e51b815260206004820152601e60248201527f4361724272696467653a207a65726f207374796c7573206164647265737300006044820152606490fd5b5f80fd5b634e487b7160e01b5f52604160045260245ffdfe60806040526004361015610011575f80fd5b5f3560e01c80630427a3e914610aea578063297a1f5614610acd5780634149116c1461058e5780635e8be1901461044a5780636a38ddb3146103cf578063717bdfbe146103a85780638da5cb5b14610380578063cd2ee947146102c0578063ef9b3f2614610291578063f2645dbe146101d7578063f7746e36146101345763ff9a78f71461009d575f80fd5b346101305760a0366003190112610130576004356100c660018060a01b036001541633146111c6565b805f5260046020526100de60ff60405f2054166112eb565b805f52600360205260405f2060243560018201556044356005820155606435600682015560076084359101557f935eeddcdb911de1c078d5ba3c0138d64d317e34145e127419eaa373421d051a5f80a2005b5f80fd5b34610130576020366003190112610130576004355f5260036020526101a660405f2061015f81611124565b9060018101549060028101549061017860038201611124565b6101be6004830154946005840154926007600686015495015495604051998a99610100808c528b0190611020565b9260208a015260408901528782036060890152611020565b93608086015260a085015260c084015260e08301520390f35b34610130575f366003190112610130576002545f805b82811061026057506101fe906112a5565b905f805b828110610223576040516020808252819061021f9082018761105e565b0390f35b805f52600460205260ff60405f205416610240575b600101610202565b906102588183610252600194886112d7565b5261125b565b919050610238565b805f52600460205260ff60405f20541661027d575b6001016101ed565b9061028960019161125b565b919050610275565b34610130576020366003190112610130576004355f526004602052602060ff60405f2054166040519015158152f35b34610130576020366003190112610130576004356001600160a01b0381811691829003610130576102f6816001541633146111c6565b811561033b575f54826bffffffffffffffffffffffff60a01b8216175f55167f3d4662559120f83b8265467a0c443930e64bf7fdc5ac4fe77f54044544a2d3c45f80a3005b60405162461bcd60e51b815260206004820152601e60248201527f4361724272696467653a207a65726f207374796c7573206164647265737300006044820152606490fd5b34610130575f366003190112610130576001546040516001600160a01b039091168152602090f35b34610130575f366003190112610130575f546040516001600160a01b039091168152602090f35b34610130576020366003190112610130576004356103f860018060a01b036001541633146111c6565b805f52600460205261041060ff60405f2054166112eb565b805f52600460205260405f2060ff1981541690557f3be6fe42407ad49db2245b58ec66dde8cf64b629871296152efce17988637b955f80a2005b346101305760208060031936011261013057610553906004355f60e0604051610472816110c9565b6060815282858201528260408201526060808201528260808201528260a08201528260c08201520152805f52600482526104b260ff60405f2054166112eb565b5f526003815260405f20604051916104c9836110c9565b6104d282611124565b8352600182015491818401928352600281015490604085019182526104f960038201611124565b606086019081526105726004830154956080880196875260058401549260a08901938452600760068601549560c08b0196875201549560e08a019687526040519a8b9a898c5251610100809a8d01526101208c0190611020565b925160408b01525160608a015251888203601f190160808a0152611020565b945160a08701525160c08601525160e085015251908301520390f35b34610130576101003660031901126101305760043567ffffffffffffffff80821161013057366023830112156101305781600401351161013057366024826004013560051b8301011161013057606490806004013515610a8a575f805b82600401358110610a4757508015610a03578060031b8181046008036109e557610614906112a5565b9161061e826112a5565b905f805b826004013582106107f1575050505f806040516020956106a58261066089820193631052445b60e21b8552610100602484015261012483019061105e565b60243560448301526044358b8301528a35608483015260843560a483015260a43560c483015260c43560e483015260e43561010483015203601f198101845283611102565b82549151916001600160a01b03165afa933d156107e9573d946106c786611209565b956106d56040519788611102565b86523d5f8688013e5b156107a75760408551106107655760408580518101031261013057604084860151950151928510156107235750604093610717916112d7565b51918351928352820152f35b60405162461bcd60e51b815260048101859052602481018590527f4361724272696467653a2062616420696e6465782066726f6d207374796c75736044820152fd5b60405162461bcd60e51b815260048101859052601e60248201527f4361724272696467653a20626164207374796c757320726573706f6e736500006044820152fd5b60405162461bcd60e51b815260048101859052601d60248201527f4361724272696467653a207374796c75732063616c6c206661696c65640000006044820152fd5b6060946106de565b61080382846004013560248601611269565b355f5260206004815260ff60405f205416156109f95761082b83856004013560248701611269565b355f526003815260405f2060405191610843836110c9565b61084c82611124565b8352600182015490830190815260076002830154926040850193845261087460038201611124565b606086015260048101546080860152600581015460a0860152600681015460c086015201548060e0850152848060031b04600814851517156109e5576108bd8560031b8b6112d7565b525160018460031b01808560031b116109e5576108da908a6112d7565b525160028360031b018360031b116109e5576108fc60028460031b01896112d7565b5260c0810151600383811b018360031b116109e557610920600384811b01896112d7565b52608081015160048360031b018360031b116109e55761094660048460031b01896112d7565b5260a081015160058360031b018360031b116109e55761097b9160609161097360058660031b018b6112d7565b520151611337565b60068260031b018260031b116109e55761099b60068360031b01886112d7565b5260078160031b01808260031b116109e5576001915f6109be6109dc938a6112d7565b526109d184866004013560248801611269565b3561025282886112d7565b915b0190610622565b634e487b7160e01b5f52601160045260245ffd5b50906001906109de565b60405162461bcd60e51b815260206004820152601860248201527f4361724272696467653a206e6f2076616c69642063617273000000000000000060448201528390fd5b610a5981846004013560248601611269565b355f52600460205260ff60405f205416610a76575b6001016105eb565b90610a8260019161125b565b919050610a6e565b5060405162461bcd60e51b815260206004820152601760248201527f4361724272696467653a20656d707479206361724964730000000000000000006044820152fd5b34610130575f366003190112610130576020600254604051908152f35b34610130576101003660031901126101305760043567ffffffffffffffff811161013057610b1c903690600401610ff2565b9060643567ffffffffffffffff811161013057610b3d903690600401610ff2565b610b5560018060a09594951b036001541633146111c6565b606460a43511610fad576101f460c43511610f685761076c608435101580610f5a575b15610f1f57610bb56002549360405192610b91846110c9565b610b9c368887611225565b8452602435602085015260443560408501523691611225565b6060820152608435608082015260a43560a082015260c43560c082015260e43560e0820152825f52600360205260405f2090805180519067ffffffffffffffff8211610e4b578190610c078554611091565b601f8111610ed2575b50602090601f8311600114610e6a575f92610e5f575b50508160011b915f199060031b1c19161782555b6020810151600183015560408101516002830155606081015180519067ffffffffffffffff8211610e4b57610c726003850154611091565b601f8111610e07575b509160209693918695938890601f8311600114610d69577fc7e9162c67bcf36a9897d9c95466ec8a031fc9ba47d5a3989c060dd040c2c015969593836060969460079460e0945f92610d5e575b50508160011b915f199060031b1c19161760038501555b6080810151600485015560a0810151600585015560c081015160068501550151910155845f526004875260405f20600160ff19825416179055610d2360025461125b565b6002558260405193849260408452816040850152848401375f82820184015260e43588830152601f01601f19168101030190a2604051908152f35b015190508d80610cc8565b60038594939298979695015f52895f20905f5b601f1984168110610ded57509260018360e0937fc7e9162c67bcf36a9897d9c95466ec8a031fc9ba47d5a3989c060dd040c2c0159a9b6060999897600797601f19811610610dd5575b505050811b016003850155610cdf565b01515f1960f88460031b161c191690558d8080610dc5565b818a01518355988b01988a98506001909201918b01610d7c565b600385015f5260205f20601f840160051c810160208510610e44575b601f830160051c82018110610e39575050610c7b565b5f8155600101610e23565b5080610e23565b634e487b7160e01b5f52604160045260245ffd5b015190508780610c26565b9250845f5260205f20905f935b601f1984168510610eb7576001945083601f19811610610e9f575b505050811b018255610c3a565b01515f1960f88460031b161c19169055878080610e92565b81810151835560209485019460019093019290910190610e77565b909150845f5260205f20601f840160051c810160208510610f18575b90849392915b601f830160051c82018110610f0a575050610c10565b5f8155859450600101610ef4565b5080610eee565b60405162461bcd60e51b815260206004820152601360248201527221b0b9213934b233b29d103130b2103cb2b0b960691b6044820152606490fd5b506108346084351115610b78565b60405162461bcd60e51b815260206004820152601760248201527f4361724272696467653a20726174696e67203e203530300000000000000000006044820152606490fd5b60405162461bcd60e51b815260206004820152601e60248201527f4361724272696467653a20696e746572696f7253636f7265203e2031303000006044820152606490fd5b9181601f840112156101305782359167ffffffffffffffff8311610130576020838186019501011161013057565b91908251928382525f5b84811061104a575050825f602080949584010152601f8019910116010190565b60208183018101518483018201520161102a565b9081518082526020808093019301915f5b82811061107d575050505090565b83518552938101939281019260010161106f565b90600182811c921680156110bf575b60208310146110ab57565b634e487b7160e01b5f52602260045260245ffd5b91607f16916110a0565b610100810190811067ffffffffffffffff821117610e4b57604052565b6040810190811067ffffffffffffffff821117610e4b57604052565b90601f8019910116810190811067ffffffffffffffff821117610e4b57604052565b9060405191825f825461113681611091565b908184526020946001916001811690815f146111a45750600114611166575b50505061116492500383611102565b565b5f90815285812095935091905b81831061118c57505061116493508201015f8080611155565b85548884018501529485019487945091830191611173565b9250505061116494925060ff191682840152151560051b8201015f8080611155565b156111cd57565b60405162461bcd60e51b815260206004820152601460248201527321b0b9213934b233b29d103737ba1037bbb732b960611b6044820152606490fd5b67ffffffffffffffff8111610e4b57601f01601f191660200190565b92919261123182611209565b9161123f6040519384611102565b829481845281830111610130578281602093845f960137010152565b5f1981146109e55760010190565b91908110156112795760051b0190565b634e487b7160e01b5f52603260045260245ffd5b67ffffffffffffffff8111610e4b5760051b60200190565b906112af8261128d565b6112bc6040519182611102565b82815280926112cd601f199161128d565b0190602036910137565b80518210156112795760209160051b010190565b156112f257565b60405162461bcd60e51b815260206004820152601d60248201527f4361724272696467653a2063617220646f6573206e6f742065786973740000006044820152606490fd5b6020815191012067656c65637472696360c01b6020604051611358816110e6565b6008815201527f94c015f872230bbfa548b1674add825db2320e57a17ec41158970feb678fbd9a811461147257651a1e589c9a5960d21b602060405161139d816110e6565b6006815201527f2df4addaf212dbcc2846b1151e534a26091c6e85573ee5530aef4bd0b8d57658811461146a57651c195d1c9bdb60d21b60206040516113e2816110e6565b6006815201527f553f57aa0962cf2d9ff54fa4f0c31415a3dce3fa73d8ed0ec1ca02ab460734a68114611462577f7d9687b68d44f8cf2fe38d013085abfd0d878a06cfcbb774e11347e6a49bb94e9065191a595cd95b60d21b6020604051611449816110e6565b6006815201521461145b57620186a090565b6203d09090565b506207a12090565b50620b71b090565b50620f42409056fea26469706673582212206586425093776981bf74a6478dc8be414618518adafbfc822a1227fbfea3558864736f6c63430008170033\n";

    private static String librariesLinkedBinary;

    public static final String FUNC_ADDCAR = "addCar";

    public static final String FUNC_CARCOUNT = "carCount";

    public static final String FUNC_CAREXISTS = "carExists";

    public static final String FUNC_CARS = "cars";

    public static final String FUNC_FINDBESTCAR = "findBestCar";

    public static final String FUNC_GETALLAVAILABLECARS = "getAllAvailableCars";

    public static final String FUNC_GETCAR = "getCar";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_REMOVECAR = "removeCar";

    public static final String FUNC_STYLUSRANKER = "stylusRanker";

    public static final String FUNC_UPDATECAR = "updateCar";

    public static final String FUNC_UPDATESTYLUSADDRESS = "updateStylusAddress";

    public static final Event CARADDED_EVENT = new Event("CarAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event CARREMOVED_EVENT = new Event("CarRemoved", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}));
    ;

    public static final Event CARUPDATED_EVENT = new Event("CarUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}));
    ;

    public static final Event STYLUSADDRESSUPDATED_EVENT = new Event("StylusAddressUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected CarBridge(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CarBridge(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected CarBridge(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected CarBridge(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> addCar(String brand, BigInteger mileage,
            BigInteger enginePower, String fuelType, BigInteger year, BigInteger interiorScore,
            BigInteger rating, BigInteger pricePerDay) {
        final Function function = new Function(
                FUNC_ADDCAR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(brand), 
                new org.web3j.abi.datatypes.generated.Uint256(mileage), 
                new org.web3j.abi.datatypes.generated.Uint256(enginePower), 
                new org.web3j.abi.datatypes.Utf8String(fuelType), 
                new org.web3j.abi.datatypes.generated.Uint256(year), 
                new org.web3j.abi.datatypes.generated.Uint256(interiorScore), 
                new org.web3j.abi.datatypes.generated.Uint256(rating), 
                new org.web3j.abi.datatypes.generated.Uint256(pricePerDay)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> carCount() {
        final Function function = new Function(FUNC_CARCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Boolean> carExists(BigInteger param0) {
        final Function function = new Function(FUNC_CAREXISTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Tuple8<String, BigInteger, BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger>> cars(
            BigInteger param0) {
        final Function function = new Function(FUNC_CARS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple8<String, BigInteger, BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger>>(function,
                new Callable<Tuple8<String, BigInteger, BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple8<String, BigInteger, BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger> call(
                            ) throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple8<String, BigInteger, BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (String) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue(), 
                                (BigInteger) results.get(7).getValue());
                    }
                });
    }

    public RemoteFunctionCall<Tuple2<BigInteger, BigInteger>> findBestCar(List<BigInteger> carIds,
            BigInteger weightPrice, BigInteger weightMileage, BigInteger weightPower,
            BigInteger weightRating, BigInteger weightYear, BigInteger weightInterior,
            BigInteger weightFuel) {
        final Function function = new Function(FUNC_FINDBESTCAR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(carIds, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(weightPrice), 
                new org.web3j.abi.datatypes.generated.Uint256(weightMileage), 
                new org.web3j.abi.datatypes.generated.Uint256(weightPower), 
                new org.web3j.abi.datatypes.generated.Uint256(weightRating), 
                new org.web3j.abi.datatypes.generated.Uint256(weightYear), 
                new org.web3j.abi.datatypes.generated.Uint256(weightInterior), 
                new org.web3j.abi.datatypes.generated.Uint256(weightFuel)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<BigInteger, BigInteger>>(function,
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<List> getAllAvailableCars() {
        final Function function = new Function(FUNC_GETALLAVAILABLECARS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<CarData> getCar(BigInteger carId) {
        final Function function = new Function(FUNC_GETCAR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(carId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<CarData>() {}));
        return executeRemoteCallSingleValueReturn(function, CarData.class);
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> removeCar(BigInteger carId) {
        final Function function = new Function(
                FUNC_REMOVECAR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(carId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> stylusRanker() {
        final Function function = new Function(FUNC_STYLUSRANKER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> updateCar(BigInteger carId, BigInteger mileage,
            BigInteger interiorScore, BigInteger rating, BigInteger pricePerDay) {
        final Function function = new Function(
                FUNC_UPDATECAR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(carId), 
                new org.web3j.abi.datatypes.generated.Uint256(mileage), 
                new org.web3j.abi.datatypes.generated.Uint256(interiorScore), 
                new org.web3j.abi.datatypes.generated.Uint256(rating), 
                new org.web3j.abi.datatypes.generated.Uint256(pricePerDay)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateStylusAddress(String _newStylusRanker) {
        final Function function = new Function(
                FUNC_UPDATESTYLUSADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _newStylusRanker)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static List<CarAddedEventResponse> getCarAddedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CARADDED_EVENT, transactionReceipt);
        ArrayList<CarAddedEventResponse> responses = new ArrayList<CarAddedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CarAddedEventResponse typedResponse = new CarAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.carId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.brand = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.pricePerDay = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static CarAddedEventResponse getCarAddedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CARADDED_EVENT, log);
        CarAddedEventResponse typedResponse = new CarAddedEventResponse();
        typedResponse.log = log;
        typedResponse.carId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.brand = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.pricePerDay = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<CarAddedEventResponse> carAddedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getCarAddedEventFromLog(log));
    }

    public Flowable<CarAddedEventResponse> carAddedEventFlowable(DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CARADDED_EVENT));
        return carAddedEventFlowable(filter);
    }

    public static List<CarRemovedEventResponse> getCarRemovedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CARREMOVED_EVENT, transactionReceipt);
        ArrayList<CarRemovedEventResponse> responses = new ArrayList<CarRemovedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CarRemovedEventResponse typedResponse = new CarRemovedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.carId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static CarRemovedEventResponse getCarRemovedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CARREMOVED_EVENT, log);
        CarRemovedEventResponse typedResponse = new CarRemovedEventResponse();
        typedResponse.log = log;
        typedResponse.carId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<CarRemovedEventResponse> carRemovedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getCarRemovedEventFromLog(log));
    }

    public Flowable<CarRemovedEventResponse> carRemovedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CARREMOVED_EVENT));
        return carRemovedEventFlowable(filter);
    }

    public static List<CarUpdatedEventResponse> getCarUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CARUPDATED_EVENT, transactionReceipt);
        ArrayList<CarUpdatedEventResponse> responses = new ArrayList<CarUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CarUpdatedEventResponse typedResponse = new CarUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.carId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static CarUpdatedEventResponse getCarUpdatedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CARUPDATED_EVENT, log);
        CarUpdatedEventResponse typedResponse = new CarUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.carId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<CarUpdatedEventResponse> carUpdatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getCarUpdatedEventFromLog(log));
    }

    public Flowable<CarUpdatedEventResponse> carUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CARUPDATED_EVENT));
        return carUpdatedEventFlowable(filter);
    }

    public static List<StylusAddressUpdatedEventResponse> getStylusAddressUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(STYLUSADDRESSUPDATED_EVENT, transactionReceipt);
        ArrayList<StylusAddressUpdatedEventResponse> responses = new ArrayList<StylusAddressUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            StylusAddressUpdatedEventResponse typedResponse = new StylusAddressUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.oldAddress = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newAddress = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static StylusAddressUpdatedEventResponse getStylusAddressUpdatedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(STYLUSADDRESSUPDATED_EVENT, log);
        StylusAddressUpdatedEventResponse typedResponse = new StylusAddressUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.oldAddress = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.newAddress = (String) eventValues.getIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<StylusAddressUpdatedEventResponse> stylusAddressUpdatedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getStylusAddressUpdatedEventFromLog(log));
    }

    public Flowable<StylusAddressUpdatedEventResponse> stylusAddressUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(STYLUSADDRESSUPDATED_EVENT));
        return stylusAddressUpdatedEventFlowable(filter);
    }

    @Deprecated
    public static CarBridge load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new CarBridge(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static CarBridge load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CarBridge(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static CarBridge load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new CarBridge(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static CarBridge load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new CarBridge(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<CarBridge> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider, String _stylusRanker) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _stylusRanker)));
        return deployRemoteCall(CarBridge.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    public static RemoteCall<CarBridge> deploy(Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider, String _stylusRanker) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _stylusRanker)));
        return deployRemoteCall(CarBridge.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<CarBridge> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit, String _stylusRanker) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _stylusRanker)));
        return deployRemoteCall(CarBridge.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<CarBridge> deploy(Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit, String _stylusRanker) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _stylusRanker)));
        return deployRemoteCall(CarBridge.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }

    public static void linkLibraries(List<Contract.LinkReference> references) {
        librariesLinkedBinary = linkBinaryWithReferences(BINARY, references);
    }

    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }

    public static class CarData extends DynamicStruct {
        public String brand;

        public BigInteger mileage;

        public BigInteger enginePower;

        public String fuelType;

        public BigInteger year;

        public BigInteger interiorScore;

        public BigInteger rating;

        public BigInteger pricePerDay;

        public CarData(String brand, BigInteger mileage, BigInteger enginePower, String fuelType,
                BigInteger year, BigInteger interiorScore, BigInteger rating,
                BigInteger pricePerDay) {
            super(new org.web3j.abi.datatypes.Utf8String(brand), 
                    new org.web3j.abi.datatypes.generated.Uint256(mileage), 
                    new org.web3j.abi.datatypes.generated.Uint256(enginePower), 
                    new org.web3j.abi.datatypes.Utf8String(fuelType), 
                    new org.web3j.abi.datatypes.generated.Uint256(year), 
                    new org.web3j.abi.datatypes.generated.Uint256(interiorScore), 
                    new org.web3j.abi.datatypes.generated.Uint256(rating), 
                    new org.web3j.abi.datatypes.generated.Uint256(pricePerDay));
            this.brand = brand;
            this.mileage = mileage;
            this.enginePower = enginePower;
            this.fuelType = fuelType;
            this.year = year;
            this.interiorScore = interiorScore;
            this.rating = rating;
            this.pricePerDay = pricePerDay;
        }

        public CarData(Utf8String brand, Uint256 mileage, Uint256 enginePower, Utf8String fuelType,
                Uint256 year, Uint256 interiorScore, Uint256 rating, Uint256 pricePerDay) {
            super(brand, mileage, enginePower, fuelType, year, interiorScore, rating, pricePerDay);
            this.brand = brand.getValue();
            this.mileage = mileage.getValue();
            this.enginePower = enginePower.getValue();
            this.fuelType = fuelType.getValue();
            this.year = year.getValue();
            this.interiorScore = interiorScore.getValue();
            this.rating = rating.getValue();
            this.pricePerDay = pricePerDay.getValue();
        }
    }

    public static class CarAddedEventResponse extends BaseEventResponse {
        public BigInteger carId;

        public String brand;

        public BigInteger pricePerDay;
    }

    public static class CarRemovedEventResponse extends BaseEventResponse {
        public BigInteger carId;
    }

    public static class CarUpdatedEventResponse extends BaseEventResponse {
        public BigInteger carId;
    }

    public static class StylusAddressUpdatedEventResponse extends BaseEventResponse {
        public String oldAddress;

        public String newAddress;
    }
}
