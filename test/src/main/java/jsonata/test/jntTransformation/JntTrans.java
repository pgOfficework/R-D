package jsonata.test.jntTransformation;

public class JntTrans {

	public static String contact="{\r\n"+ 
			"\"AUD_SRC_SYS_ID\" : '1',\r\n" + 
			"\r\n" + 
			"\"AUD_SRC_SYS_NM\" : 'IDIT',\r\n" + 
			"\"language\":	$count(contactHistory)!=0?\r\n" + 
			"    $filter(contactHistory, function($v, $i, $a){\r\n" + 
			"        $v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))\r\n" + 
			"        }).userContactVO.language.desc,\r\n" + 
			"\r\n" + 
			"\"fullName\":$count(contactHistory)!=0?$filter(contactHistory, function($v, $i, $a){$v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))}).\r\n" + 
			"(((contactFirstName!=null)?contactFirstName)&((contactMiddleName!=null)?\" \"&contactMiddleName)&((contactName!=null)?\" \"&contactName)),\r\n" + 
			"\r\n" + 
			"\"contactId\":id&'|101'&'|IDIT',\r\n" + 
			"\"_id\":$string(id),\r\n" + 
			"\"dateOfBirth\":$count(contactHistory)!=0?$filter(contactHistory, function($v, $i, $a){$v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))}).contactDateOfBirth,\r\n" + 
			"\"contactNumber\":$string(id),\r\n" + 
			"\"preferredContactMethod\":(preferredDeliveryType!=null)?{\"id\":preferredDeliveryType.id,\"description\":preferredDeliveryType.desc},\r\n" + 
			"\"firstName\":($count(contactHistory)!=0?$filter(contactHistory, function($v, $i, $a){$v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))}).contactFirstName!=null)?\r\n" + 
			"                $count(contactHistory)!=0?$filter(contactHistory, function($v, $i, $a){$v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))}).contactFirstName,\r\n" + 
			"\"middleName\":($count(contactHistory)!=0?$filter(contactHistory, function($v, $i, $a){$v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))}).contactMiddleName!=null)?\r\n" + 
			"                $filter(contactHistory, function($v, $i, $a){$v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))}).contactMiddleName,\r\n" + 
			"\"lastName\":($count(contactHistory)!=0?$filter(contactHistory, function($v, $i, $a){$v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))}).contactName!=null)?\r\n" + 
			"                $filter(contactHistory, function($v, $i, $a){$v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))}).contactName,\r\n" + 
			"\"title\":(titleVO!=null)?{\"id\":titleVO.id,\"description\":titleVO.desc},\r\n" + 
			"\"gender\":(genderVO!=null)?{\"id\":genderVO.id,\"description\":genderVO.desc},\r\n" + 
			"\r\n" + 
			"\"marital_status\":	(familyStatusVO!=null or adultChildren!=null)?(((familyStatusVO!=null)?familyStatusVO.desc)&((adultChildren!=null)?\"+\"&adultChildren)),\r\n" + 
			"\r\n" + 
			"\"nationality\":(nationality!=null)?{\"id\":nationality.id,\"description\":nationality.desc},\r\n" + 
			"\"birthCountry\":(birthCountryVO!=null)?{\"id\":birthCountryVO.id,\"description\":birthCountryVO.desc},\r\n" + 
			"\"education\":(educationVO!=null)?{\"id\":educationVO.id,\"description\":educationVO.desc},\r\n" + 
			"\"profession\":(professionVO!=null)?{\"id\":professionVO.id,\"description\":professionVO.desc},\r\n" + 
			"\"telephoneNumbers\":[($count(contactTelephone)!=0 & contactTelephone!=null)?$map(contactTelephone, function($v, $i, $a) {\r\n" + 
			"        {\r\n" + 
			"            \"id\":$string($v.id),\r\n" + 
			"            \"telephoneNumber\":($v.telephoneNumber!=null)?$string($v.telephoneNumber),\r\n" + 
			"            \"countryDialCode\":($v.countryDialCode!=null)?{\r\n" + 
			"                    \"id\":$v.countryDialCode.id,\r\n" + 
			"                    \"description\":($v.countryDialCode.desc!=null)?$v.countryDialCode.desc},\r\n" + 
			"            \"telephonePrefix\":($v.telephoneExtension!=null)?$v.telephoneExtension,\r\n" + 
			"            \"telephoneType\":($v.telephoneType!=null)?{\r\n" + 
			"                    \"id\":$v.telephoneType.id,\r\n" + 
			"                    \"description\":($v.telephoneType.desc!=null)?$v.telephoneType.desc}\r\n" + 
			"        }\r\n" + 
			"})],\r\n" + 
			"\"emails\":($count(contactEmail)!=0 and contactEmail!=null)?[$map(contactEmail, function($v, $i, $a) {\r\n" + 
			"   {\r\n" + 
			"       \"id\":$v.id,\r\n" + 
			"       \"email\":($v.email!=null)?$v.email,\r\n" + 
			"       \"emailType\":($v.emailTypeVO!=null)?{\r\n" + 
			"            \"id\":$v.emailTypeVO.id,\r\n" + 
			"            \"email\":($v.emailTypeVO.desc!=null)?$v.emailTypeVO.desc\r\n" + 
			"        }\r\n" + 
			"    }\r\n" + 
			"})],\r\n" + 
			"\"bankAccounts\":($count(contactBankAccount)!=0 and contactBankAccount!=null)?[$map(contactBankAccount, function($v, $i, $a) {\r\n" + 
			"        {\r\n" + 
			"        \"country\":{\r\n" + 
			"                    \"id\":$v.id,\r\n" + 
			"                    \"description\":($v.desc!=null)?$v.desc},\r\n" + 
			"        \"bankName\":($v.bankName!=null)?$v.bankName,\r\n" + 
			"        \"branchName\":($v.branchName!=null)?$v.branchName,\r\n" + 
			"        \"accountNumberLastDigits\":($v.accountNrEncrypted!=null)?$v.accountNrEncrypted\r\n" + 
			"        } })],\r\n" + 
			"\"addresses\":$count(contactAddress)!=0?$count(contactAddress)!=0?$map(contactAddress, function($v, $i, $a) {\r\n" + 
			"    {\r\n" + 
			"        \"id\":$v.addressVO.id,\r\n" + 
			"        \"street\":($v.addressVO.streetName!=null)?$v.addressVO.streetName,\r\n" + 
			"        \"house\":($v.addressVO.houseNr!=null)?$v.addressVO.houseNr,\r\n" + 
			"        \"city\":($v.addressVO.cityName!=null)?$v.addressVO.cityName,\r\n" + 
			"        \"postalCode\":($v.addressVO.zipCode!=null)?$v.addressVO.zipCode,\r\n" + 
			"        \"addressType\":($v.addressTypeVO!=null){\r\n" + 
			"            \"id\":$v.addressTypeVO.id,\r\n" + 
			"            \"description\":($v.addressTypeVO.desc!=null)?$v.addressTypeVO.desc},\r\n" + 
			"        \"country\":($v.addressVO.countryVO!=null)?{\r\n" + 
			"            \"id\":$v.addressVO.countryVO.id,\r\n" + 
			"            \"description\":($v.addressVO.countryVO.desc!=null)?$v.addressVO.countryVO.desc}\r\n" + 
			"    } })\r\n" + 
			"}";

	public static String dateDiffTest="{\r\n" + 
			"    \"duration\":$dateDiff(\"yyyy-MM-dd'T'HH:mm:ss\",startDate,endDate)\r\n" + 
			"}"; 
	
	public static String payment="{\r\n" + 
			"    \"_id\":id,\r\n" + 
			"\"AUD_SRC_SYS_ID\" : '1',\r\n" + 
			"\r\n" + 
			"\"AUD_SRC_SYS_NM\" : 'IDIT',\r\n" + 
			"    \"policyHolderId\":$count(policyContactVO)!=0?$filter(policyContactVO, function($v, $i, $a){$v.policyContactRoleVO.desc=\"Policyholder\"}).contactId,\r\n" + 
			"    \"policyHolderName\":$count(policyContactVO)!=0?$filter(policyContactVO, function($v, $i, $a){$v.policyContactRoleVO.desc=\"Policyholder\"}).(contactVO.firstName&\" \"&contactVO.MiddleName&\" \"&contactVO.name),\r\n" + 
			"    \"policyNumber\":$string(paymentSchedulerVO.installmentVO[0].policyId),\r\n" + 
			"    \"policyHeaderId\":policyHeaderId,\r\n" + 
			"    \"product\":$count(policyHeaderVO.productVO)!=0?{\r\n" + 
			"        \"description\":policyHeaderVO.productVO.desc,\r\n" + 
			"        \"id\":policyHeaderVO.productVO.id},\r\n" + 
			"    \"statusDate\":policyHeaderVO.statusDate,\r\n" + 
			"    \"paymenTerms\":paymentSchedulerVO.paymentTermsVO.desc,\r\n" + 
			"    \"collectionMethod\":paymentSchedulerVO.collectionMethodVO.desc,\r\n" + 
			"\r\n" + 
			"     \"lastPaidAmount\":$count($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.invoiceNumber!=null}))?$string($filter($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.invoiceNumber!=null}), function($v, $i, $a){$v.invoiceNumber.$number() = $max($a.invoiceNumber.$number())})\r\n" + 
			"        .paidInstallmentAmount),\r\n" + 
			"\r\n" + 
			"    \"lastAmountpaidDate\":$count($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.invoiceNumber!=null}))?$filter($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.invoiceNumber!=null}), function($v, $i, $a){$v.invoiceNumber.$number() = $max($a.invoiceNumber.$number())}).startPeriod,\r\n" + 
			"\r\n" + 
			"    \"nextPaymentDueDate\":$count($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.installmentNumber!=null}))?$filter($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.installmentNumber!=null}), function($v, $i, $a){$v.installmentNumber = $max($a.installmentNumber)}).dueDate,\r\n" + 
			"\r\n" + 
			"    \"nextPremiumAmount\":$count($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.installmentNumber!=null}))?$string($filter($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.installmentNumber!=null}), function($v, $i, $a){$v.installmentNumber = $max($a.installmentNumber)}).storedInstallmentAmount),\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"    \"paidAmount\" : $string($sum($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))\r\n" + 
			"        }).installmentStatusVOList.installmentHistoryVO.installmentDetailsVO.amountVO.amount)),\r\n" + 
			"    \r\n" + 
			"\r\n" + 
			"    \"yearlyPremium\":(paymentSchedulerVO.installmentVO.installmentHistoryVO.installmentDetailsVO.amountVO.amount!=null)?$string($sum(paymentSchedulerVO.installmentVO.installmentHistoryVO.installmentDetailsVO.amountVO.amount)),\r\n" + 
			"    \"collection\":(paymentSchedulerVO.installmentVO!=null & $count(paymentSchedulerVO.installmentVO)!=0)?$map(paymentSchedulerVO.installmentVO,function($v,$i,$a){\r\n" + 
			"	{\r\n" + 
			"                \"billingType\" :$v.installmentStatusVOList.billingTypeVO.desc,\r\n" + 
			"                \"collectionMethod\":{\"description\":$v.collectionMethodVO.desc,\"id\":$v.collectionMethodVO.id},\r\n" + 
			"                \"collectionDate\":$v.collectionDate,\r\n" + 
			"                \"dueDate\":$v.dueDate,\r\n" + 
			"                \"estimatedAmount\":$count($v.estimatedAmountVO)!=0?{\r\n" + 
			"                    \"accountAmount\":($v.estimatedAmountVO.amountSystem!=null)?$string($v.estimatedAmountVO.amountSystem),\r\n" + 
			"                    \"amount\":($v.estimatedAmountVO.amount!=null)?$string($v.estimatedAmountVO.amount),\r\n" + 
			"                    \"currency\":$count($v.estimatedAmountVO.currencyVO)!=0?{\r\n" + 
			"                        \"description\":$v.estimatedAmountVO.currencyVO.desc,\r\n" + 
			"                        \"id\":$v.estimatedAmountVO.currencyVO.id}\r\n" + 
			"                        },\r\n" + 
			"                \"graceDate\":$v.graceDate\r\n" + 
			"}\r\n" + 
			"}),\r\n" + 
			"    \"installmentDetails\":(paymentSchedulerVO.installmentVO!=null & $count(paymentSchedulerVO.installmentVO)!=0)?$map(paymentSchedulerVO.installmentVO,function($v,$i,$a){\r\n" + 
			"	{\r\n" + 
			"    \"basicPremiumAmount\":($v.lastInstallmentHistoryVO.installmentDetailsVO!=null & $count($v.lastInstallmentHistoryVO.installmentDetailsVO)!=0)?$map($v.lastInstallmentHistoryVO.installmentDetailsVO,function($v,$i,$a){\r\n" + 
			"        {\r\n" + 
			"            \"id\":$v.id,\r\n" + 
			"            \"accountAmount\":($v.basicPremiumAmountVO.amountSystem!=null)?$string($v.basicPremiumAmountVO.amountSystem),\r\n" + 
			"            \"amount\":($v.basicPremiumAmountVO.amount!=null)?$string($v.basicPremiumAmountVO.amount),\r\n" + 
			"            \"amountRate\":$v.basicPremiumAmountVO.amountRate,\r\n" + 
			"            \"currency\":$count($v.basicPremiumAmountVO.currencyVO)!=0?{\r\n" + 
			"                \"description\":$v.basicPremiumAmountVO.currencyVO.desc,\r\n" + 
			"                \"id\":$v.basicPremiumAmountVO.currencyVO.id},\r\n" + 
			"            \"coverDetails\":$count($v)!=0?{\r\n" + 
			"                \"coverFrom\":$v.coverFrom,\r\n" + 
			"                \"coverTo\":$v.coverTo,\r\n" + 
			"                \"coverId\":$v.coverId\r\n" + 
			"                    }\r\n" + 
			"        }\r\n" + 
			"            }),\r\n" + 
			"        \"taxAmount\":$count($v.taxesAmountVO)!=0?{\r\n" + 
			"            \"amount\":($v.taxesAmountVO.amount!=null)?$string($v.taxesAmountVO.amount),\r\n" + 
			"            \"currency\":$v.taxesAmountVO.currencyVO.desc},\r\n" + 
			"\r\n" + 
			"        \"totalAmount\":($v.lastInstallmentHistoryVO.installmentDetailsVO.originalAmountVO !=null & $count($v.lastInstallmentHistoryVO.installmentDetailsVO.originalAmountVO)!=0)?$map($v.lastInstallmentHistoryVO.installmentDetailsVO.originalAmountVO,function($v,$i,$a){\r\n" + 
			"        {\r\n" + 
			"            \"accountAmount\":($v.amountSystem!=null)?$string($v.amountSystem),\r\n" + 
			"            \"amount\":($v.amount!=null)?$string($v.amount),\r\n" + 
			"            \"amountRate\":$v.amountRate,\r\n" + 
			"            \"currency\":$count($v.currencyVO)!=0?{\r\n" + 
			"                \"description\":$v.currencyVO.desc,\r\n" + 
			"                \"id\":$v.currencyVO.id}\r\n" + 
			"        }\r\n" + 
			"            })\r\n" + 
			"    }\r\n" + 
			"}),\r\n" + 
			"    \"installmentFee\":(paymentSchedulerVO.installmentVO!=null & $count(paymentSchedulerVO.installmentVO)!=0)?$map(paymentSchedulerVO.installmentVO,function($v,$i,$a){\r\n" + 
			"        {\r\n" + 
			"        \"accountAmount\":($v.estimatedAmountVO.amountSystem!=null)?$string($v.estimatedAmountVO.amountSystem),\r\n" + 
			"            \"amount\":$string($v.estimatedAmountVO.amount),\r\n" + 
			"            \"currency\":$count($v.estimatedAmountVO.currencyVO)!=0?{\r\n" + 
			"                \"description\":$v.estimatedAmountVO.currencyVO.desc,\r\n" + 
			"                \"id\":$v.estimatedAmountVO.currencyVO.id},\r\n" + 
			"            \"installmentId\":$v.id&'101'&'IDIT',\r\n" + 
			"            \"installmentNumber\":$v.id,\r\n" + 
			"            \"installmentStatus\":$count($v.installmentStatusTypeVO)!=0?{\r\n" + 
			"                \"description\":$v.installmentStatusTypeVO.desc,\r\n" + 
			"                \"id\":$v.installmentStatusTypeVO.id},\r\n" + 
			"            \"installmentType\":$count($v.installmentTypeVO)!=0?{\r\n" + 
			"                \"description\":$v.installmentTypeVO.desc,\r\n" + 
			"                \"id\":$v.installmentTypeVO.id},\r\n" + 
			"            \"originalDueDate\":$v.originalDueDate,\r\n" + 
			"            \"paymentTerms\":$count($v.paymentTermsVO)!=0?{\r\n" + 
			"                \"description\":$v.paymentTermsVO.desc,\r\n" + 
			"                \"id\":$v.paymentTermsVO.id}\r\n" + 
			"            }\r\n" + 
			"            }),\r\n" + 
			"    \"taxes\": (paymentSchedulerVO.installmentVO.lastInstallmentHistoryVO.installmentDetailsVO.installmentDetailTaxVOs.installmentDetailTaxAmountVOs!=null & $count(paymentSchedulerVO.installmentVO.lastInstallmentHistoryVO.installmentDetailsVO.installmentDetailTaxVOs.installmentDetailTaxAmountVOs)!=0)?$map(paymentSchedulerVO.installmentVO.lastInstallmentHistoryVO.installmentDetailsVO.installmentDetailTaxVOs.installmentDetailTaxAmountVOs,function($v,$i,$a){\r\n" + 
			"        {\r\n" + 
			"            \"accountAmount\":($v.amountVO.amountSystem!=null)?$string($v.amountVO.amountSystem),\r\n" + 
			"            \"amount\":$v.amountVO.amount,\r\n" + 
			"            \"currency\":$count($v.currencyVO)!=0?{\r\n" + 
			"                \"description\":$v.currencyVO.desc,\r\n" + 
			"                \"id\":$v.currencyVO.id}\r\n" + 
			"            }\r\n" + 
			"            }),\r\n" + 
			"    \"totalInstallmentAmount\":(paymentSchedulerVO.installmentVO.lastInstallmentHistoryVO.installmentDetailsVO)!=0?$map(paymentSchedulerVO.installmentVO.lastInstallmentHistoryVO.installmentDetailsVO,function($v,$i,$a){\r\n" + 
			"        {\r\n" + 
			"            \"accountAmount\":($v.amountVO.amountSystem!=null)?$string($v.amountVO.amountSystem),\r\n" + 
			"            \"amount\":($v.amountVO.amount!=null)?$string($v.amountVO.amount)\r\n" + 
			"        }\r\n" + 
			"            }),\r\n" + 
			"    \"totalInstallmentAmount1\":(paymentSchedulerVO.installmentVO.lastInstallmentHistoryVO.installmentDetailsVO.amountVO.amountSystem!=null)?\r\n" + 
			"    $string($sum(paymentSchedulerVO.installmentVO.lastInstallmentHistoryVO.installmentDetailsVO.amountVO.amountSystem))\r\n" + 
			"\r\n" + 
			"            \r\n" + 
			"}";

	public static String test="{\r\n" + 
			"     \"lastPaidAmount\":$count($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.invoiceNumber!=null}))?$string($filter($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.invoiceNumber!=null}), function($v, $i, $a){$v.invoiceNumber.$number() = $max($a.invoiceNumber.$number())})\r\n" + 
			"        .paidInstallmentAmount),\r\n" + 
			"\r\n" + 
			"    \"lastAmountpaidDate\":$count($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.invoiceNumber!=null}))?$filter($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.invoiceNumber!=null}), function($v, $i, $a){$v.invoiceNumber.$number() = $max($a.invoiceNumber.$number())}).startPeriod,\r\n" + 
			"\r\n" + 
			"    \"nextPaymentDueDate\":$count($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.installmentNumber!=null}))?$filter($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.installmentNumber!=null}), function($v, $i, $a){$v.installmentNumber = $max($a.installmentNumber)}).dueDate,\r\n" + 
			"\r\n" + 
			"    \"nextPremiumAmount\":$count($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.installmentNumber!=null}))?$string($filter($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.installmentNumber!=null}), function($v, $i, $a){$v.installmentNumber = $max($a.installmentNumber)}).storedInstallmentAmount),\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"    \"paidAmount\" : $string($sum($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"        $v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))\r\n" + 
			"        }).installmentStatusVOList.installmentHistoryVO.installmentDetailsVO.amountVO.amount)),\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"    \"emails\":[$map($filter(paymentSchedulerVO.installmentVO, function($v, $i, $a){\r\n" + 
			"         $v.invoiceNumber!= null}), function($v, $i, $a) {$number($v.invoiceNumber)})],\r\n" + 
			"\r\n" + 
			"    \"test\":$filter(paymentSchedulerVO.installmentVO,function($v, $i, $a){\r\n" + 
			"        $v.invoiceNumber!=null\r\n" + 
			"    }).invoiceNumber.$number()\r\n" + 
			"}";
	
	public static String claim="{\r\n" + 
			" \"paymentDate\":$count(fullRelatedClaimContacts.claimantAssetList.damageList.claimTransactionList.claimTransactionLineList)!=0?\r\n" + 
			"    $filter(fullRelatedClaimContacts.claimantAssetList.damageList.claimTransactionList.claimTransactionLineList, function($v, $i, $a)\r\n" + 
			"    {$v.(amountVO.id) = $max($a.(amountVO.id))}).amountVO.updateDate,\r\n" + 
			"\r\n" + 
			"    \"test\":fullRelatedClaimContacts.claimantAssetList.damageList.claimTransactionList.claimTransactionLineList.id\r\n" + 
			"}";}
