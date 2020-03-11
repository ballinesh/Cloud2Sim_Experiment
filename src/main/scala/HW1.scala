import java.io.{BufferedWriter, File, FileWriter}
import java.text.DecimalFormat
import java.util
import java.util.{Calendar, LinkedList}

import scala.collection.mutable.ListBuffer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.cloudbus.cloudsim.{Cloudlet, CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared, Datacenter, DatacenterBroker, DatacenterCharacteristics, Host, Log, NetworkTopology, Pe, Storage, UtilizationModel, UtilizationModelFull, Vm, VmAllocationPolicySimple, VmSchedulerSpaceShared, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.lists.VmList
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple

import scala.collection.JavaConverters._
//import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.annotation.tailrec
import collection.mutable._

object HW1 {
  def main(args: Array[String]): Unit = {

    //In scala, at least with my experience learning it through this hw, you need to create an object for things to run properly
    //As a result I had to make a simulator class in order to get the Logger to work
    val sim1 = new simulator
    val sim2 = new simulator
    val sim3 = new simulator
    val sim4 = new simulator
    val sim5 = new simulator
    val sim6 = new simulator
    val sim7 = new simulator
    val sim8 = new simulator

    //The first parameter determines the characteristics of the cloudlets
    //The second parameter states the number of cloudlets
    sim1.runTheSim(1,10, "Sim1.txt")   //50000 mips cloudlet running 10 of them
    sim2.runTheSim(1,1000,"Sim2.txt")  //50000 mips cloudlet running 1000 of them
    sim3.runTheSim(2,10,"Sim3.txt")    //100   mips cloudlet running 10 of them
    sim4.runTheSim(2,1000,"Sim4.txt")  //100   mips cloudlet running 1000 of them

    //The networks have a different config
    sim5.runNetworkSim(1, "NetworkSim.txt")       //Testing a smart assign algorithm
    sim6.runNetworkSim(2,"NetworkSim2.txt")       //Applying smart algorithm with random values
    sim7.runNetworkSim(3,"MapReduce.txt")         //My map reduce implementation
    sim8.runNetworkSim(4,"MapReduceRandom.txt")   //Map reduce with different numbers
  }

}

class simulator {

  //Creating the logger, this is java so we have to put it in a logger variable where the class is the simulator
  val logger = LoggerFactory.getLogger(classOf[simulator])
  logger.info("Starting the simulator")

  //Utilization Model is a class variable for ease of use
  var model = new UtilizationModelFull()

  def runTheSim(typeOfSim: Int, workload: Int, outputName: String) {

    logger.info("Inside of the run sim method")
    val num_user = 1 // number of cloud users
    val calendar: Calendar = Calendar.getInstance // Calendar whose fields have been initialized with the current date and time.
    val trace_flag = false // trace events

    //initializing the cloudsim
    logger.info("Initializing Cloudsim")
    CloudSim.init(num_user, calendar, trace_flag)
    logger.info("Initializing complete")

    //Creating the datacenter with name "TestDataCetner"
    logger.info("Creating datacenter")
    val datacenterFourHosts4Vm = createDataCenter("4VM", 2, 4)


    //Setting up the brokers
    logger.info("Starting up brokers")
    var brokerFourHosts4VM = createBroker("Broker4VM")
    logger.info("Brokers Complete")

    //Creating a list of VM's
    logger.info("Creating VMs")
    var startingNumber = 0
    val List4VM = createVMs(startingNumber, 4, 2, brokerFourHosts4VM.getId())
    logger.info("VMs Complete")


    //Creating a list of cloudlets
    //Defining the cloudlets
    logger.info("Creating cloudlets")
    var cloudletList1 = new ListBuffer[Cloudlet]
    createCloudlets(startingNumber, workload, typeOfSim, cloudletList1, model)
    logger.info("Cloudlets complete")

    //Submitting the list of VM's and Cloudlets to the broker
    logger.info("Submitting VMs and Cloudlets")
    brokerFourHosts4VM.submitVmList(List4VM.asJava)
    brokerFourHosts4VM.submitCloudletList(cloudletList1.asJava)
    assignVM(brokerFourHosts4VM, cloudletList1.toList, List4VM)
    logger.info("VM's and Cloudlets submitted")


    //*******Starting the cloud simulation********
    CloudSim.startSimulation()
    Log.printLine()
    Log.printLine("========== OUTPUT ==========")
    Log.printLine("Cloudlet ID\t\tSTATUS\t\tData center ID\t\tVM ID\tTime\tStart Time\tFinish Time")

    //Writing to a txt file for ease of use
    writeFile(outputName,"========== OUTPUT ==========\n", false)
    writeFile(outputName, "Cloudlet ID\tData center ID\tVM ID\tTime\tStart Time\tFinish Time\n", true)

    var newList1 = brokerFourHosts4VM.getCloudletReceivedList().asScala
    printCloudletList(newList1.toList,outputName)

    CloudSim.stopSimulation()
    //*******End of simulation********


    //******Initializing the second simulation*******//
    logger.info("Setting up simulation part 2")
    CloudSim.init(num_user, calendar, trace_flag)

    //Creation of the datacenter
    logger.info("Starting up the second datacenter")
    var datacenterFourHosts8Vm = createDataCenter("8VM", 3, 4)

    //Creation of the second broker
    logger.info("Starting up the second broker")
    var brokerFourHosts8VM = createBroker("Broker8VM")

    //Creation of the second list of VMs
    logger.info("Starting up the second list of VMs")
    val List8VM = createVMs(startingNumber, 8, 3, brokerFourHosts8VM.getId())

    //Creation of the list of cloudlets
    logger.info("Starting up the second list of cloudlets")
    var cloudletList2 = new ListBuffer[Cloudlet]
    createCloudlets(startingNumber, workload, typeOfSim, cloudletList2, model)

    //Submitting the VMs and Cloudlets
    logger.info("Submitting vms and cloudlets to the broker")
    brokerFourHosts8VM.submitVmList(List8VM.asJava)
    brokerFourHosts8VM.submitCloudletList(cloudletList2.asJava)

    //Assigning cloudlets to vms
    logger.info("Assigning cloudlets to VMs")
    assignVM(brokerFourHosts8VM, cloudletList2.toList, List8VM)


    //Starting the second simulation
    logger.info("Starting the second simulation")
    CloudSim.startSimulation()

    Log.printLine()
    Log.printLine("========== OUTPUT ==========")
    Log.printLine("Cloudlet ID\t\tSTATUS\t\tData center ID\t\tVM ID\tTime\tStart Time\tFinish Time")

    //Printing to a text file for easier readability
    writeFile(outputName,"========== OUTPUT ==========\n", true)
    writeFile(outputName, "Cloudlet ID\tData center ID\tVM ID\tTime\tStart Time\tFinish Time\n", true)

    val newList2 = brokerFourHosts8VM.getCloudletReceivedList().asScala
    printCloudletList(newList2.toList,outputName)

    CloudSim.stopSimulation()

    //******Initializing the third simulation*******//
    logger.info("Setting up simulation part 3")
    CloudSim.init(num_user, calendar, trace_flag)

    //Creation of the third DataCenter
    logger.info("Starting up datacenter 3")
    val datacenterTwoHosts2Vm = createDataCenter("2VM", 4, 2)

    //Creation of the broker for this simulation
    logger.info("Starting up broker 3")
    val brokerTwoHosts2VM = createBroker("Broker2VM")

    //Creation of the VMList
    logger.info("Creation of the VM's")
    val List2VM = createVMs(startingNumber, 2, 4, brokerTwoHosts2VM.getId())

    //Creation of the third set of cloudlets
    logger.info("Creation of cloudlets")
    val cloudletList3 = new ListBuffer[Cloudlet]
    createCloudlets(startingNumber, workload, typeOfSim, cloudletList3, model)

    //Assigning the VMs to brokers
    logger.info("Assigning cloudlets to VMs")
    brokerTwoHosts2VM.submitVmList(List2VM.asJava)
    brokerTwoHosts2VM.submitCloudletList(cloudletList3.asJava)
    assignVM(brokerTwoHosts2VM, cloudletList3.toList, List2VM)

    //Starting the simulation
    logger.info("Starting the third simulation")
    CloudSim.startSimulation()

    //Printing out the header
    Log.printLine()
    Log.printLine("========== OUTPUT ==========")
    Log.printLine("Cloudlet ID\t\tSTATUS\t\tData center ID\t\tVM ID\tTime\tStart Time\tFinish Time\n")

    //Writing to a file for ease of readability
    writeFile(outputName,"========== OUTPUT ==========\n", true)
    writeFile(outputName, "Cloudlet ID\tData center ID\tVM ID\tTime\tStart Time\tFinish Time\n", true)

    //Printing out the list
    val newList3 = brokerTwoHosts2VM.getCloudletReceivedList().asScala
    printCloudletList(newList3.toList,outputName)

    CloudSim.stopSimulation()
  }

  def runNetworkSim(workType: Int, outputName: String): Unit =
  {
    logger.info("Inside of the run sim method")
    val num_user = 1 // number of cloud users
    val calendar: Calendar = Calendar.getInstance // Calendar whose fields have been initialized with the current date and time.
    val trace_flag = false // trace events

    //initializing the cloudsim
    logger.info("Initializing Cloudsim")
    CloudSim.init(num_user, calendar, trace_flag)
    logger.info("Initializing complete")

    //Creating the datacenter with name "TestDataCenter"
    logger.info("Creating datacenter")
    val datacenterFourHosts4Vm = createDataCenter("4VM", 2, 4)
    val datacenterFourHosts8Vm = createDataCenter("8VM", 3, 4)
    val datacenterTwoHosts2Vm = createDataCenter("2VM", 4, 2)

    //Setting up the brokers
    logger.info("Starting up brokers")
    val brokerFourHosts4VM = createBroker("Broker4VM")
    val brokerFourHosts8VM = createBroker("Broker8VM")
    val brokerTwoHosts2VM = createBroker("Broker2VM")
    logger.info("Brokers Complete")

    //Creating a list of VM's
    logger.info("Creating the VM's")
    val startingNumber = 0
    val List4VM = createVMs(startingNumber, 4, 2, brokerFourHosts4VM.getId())
    val List8VM = createVMs(startingNumber + 4, 8, 3, brokerFourHosts8VM.getId())
    val List2VM = createVMs(startingNumber + 12, 2, 4, brokerTwoHosts2VM.getId())
    logger.info("VM lists complete")

    //Submitting the list of VM's to the respective broker
    logger.info("Submitting the list of VM's to the respective broker")
    brokerFourHosts4VM.submitVmList(List4VM.asJava)
    brokerFourHosts8VM.submitVmList(List8VM.asJava)
    brokerTwoHosts2VM.submitVmList(List2VM.asJava)
    logger.info("Cloudlets submitted to Vm's")

    //Creating a list of cloudlets
    //Defining the cloudlets
    logger.info("Creating cloudlets")
    var cloudletList1 = new ListBuffer[Cloudlet]
    logger.info("Cloudlets created")

    if(workType == 1) //Worktype 1 is for a simple controlled network
    {
      //Creating cloudlets
      logger.info("Creating cloudlets")
      createCloudlets(startingNumber, 8, 2, cloudletList1, model)
      createCloudlets(startingNumber + 8, 8, 3, cloudletList1, model)
      createCloudlets(startingNumber + 16, 8, 4, cloudletList1, model)
      logger.info("Cloudlets Created")
    }
    else if (workType == 2)
    {
      //Creating cloudlets with random values
      logger.info("Creating random cloudlets")
      val r = scala.util.Random
      createCloudlets(startingNumber,r.nextInt(100), 5,cloudletList1,model)
      logger.info("Random Cloudlets complete")

    }
  else if (workType == 3) //This is the map/reduce implementation
  {

      //Creating a cloudlet with 100000 mips
      logger.info("Creating cloudlet")
      val cloudlet = new Cloudlet(1, 100000, 1, 500, 600, model, model, model)
      logger.info("Cloudlet created")

      //Running the map reduce algorithm on three different datacenters
      logger.info("Running map/reduce on cloudlet")
      val mips = (cloudlet.getCloudletLength / 3) + 1
      val mappedCloudlets1 = mapReduceCloudlet(0, List4VM, mips, model)
      val sizeFirst = mappedCloudlets1.size
      val mappedCloudlets2 = mapReduceCloudlet(sizeFirst, List8VM, mips, model)
      val sizeSecond = mappedCloudlets2.size
      val mappedCloudlets3 = mapReduceCloudlet(sizeSecond + sizeFirst, List2VM, mips, model)
      logger.info("Map reduce complete")

      
      //submitting cloudlet lists to the brokers
      logger.info("Submitting cloudlets")
      brokerFourHosts4VM.submitCloudletList(mappedCloudlets1.asJava)
      brokerFourHosts8VM.submitCloudletList(mappedCloudlets2.asJava)
      brokerTwoHosts2VM.submitCloudletList(mappedCloudlets3.asJava)
      logger.info("Cloudlets submitted")

      //Assigning cloudlets to lists and brokers
      logger.info("Assigning vm's and cloudlets")
      assignVM(brokerFourHosts4VM, mappedCloudlets1, List4VM)
      assignVM(brokerFourHosts8VM, mappedCloudlets2, List8VM)
      assignVM(brokerTwoHosts2VM, mappedCloudlets3, List2VM)
      logger.info("Assigning complete")
    }
    else //This is the random workload for map/reduce
      {
        
        //Creating cloudlets using random numbers
        logger.info("Creating cloudlets with random workload")
        val r = scala.util.Random
        val cloudlet = new Cloudlet(1, r.nextInt(10000), 1, 500, 600, model,model,model)
        val mips = (cloudlet.getCloudletLength / 3) + 1
        logger.info("Cloudlet complete")

        //Starting the map/reduce algorithm
        logger.info("Applying map/reduce")
        val mappedCloudlets1 = mapReduceCloudlet(0, List4VM, mips, model)
        val sizeFirst = mappedCloudlets1.size
        val mappedCloudlets2 = mapReduceCloudlet(sizeFirst, List8VM, mips, model)
        val sizeSecond = mappedCloudlets2.size
        val mappedCloudlets3 = mapReduceCloudlet(sizeSecond + sizeFirst, List2VM, mips, model)
        logger.info("Map/reduce complete")

        //Creating the three brokers
        logger.info("Creating brokers")
        brokerFourHosts4VM.submitCloudletList(mappedCloudlets1.asJava)
        brokerFourHosts8VM.submitCloudletList(mappedCloudlets2.asJava)
        brokerTwoHosts2VM.submitCloudletList(mappedCloudlets3.asJava)
        logger.info("Brokers complete")

        //Assigning the vm's we don't need smart assign because we 
        //already have the separated lists
        logger.info("Assigning vms and cloudlets")
        assignVM(brokerFourHosts4VM, mappedCloudlets1, List4VM)
        assignVM(brokerFourHosts8VM, mappedCloudlets2, List8VM)
        assignVM(brokerTwoHosts2VM, mappedCloudlets3, List2VM)
        logger.info("Assigning done")

      }

    if(workType == 1 || workType == 2) //For smmart assign algorithm
      {
        
        //We need to use Var because we need to get two values using one function
        logger.info("Submitting cloudlets")
        var submittedList1 = new ListBuffer[Cloudlet]
        var submittedList2 = new ListBuffer[Cloudlet]
        var submittedList3 = new ListBuffer[Cloudlet]
      
        //Submitting the list of Cloudlets to the respective broker using smartSubmit function
        val filterCloudlet1 = smartSubmitCloudletList(brokerTwoHosts2VM, cloudletList1.toList, submittedList1, datacenterTwoHosts2Vm)
        val filterCloudlet2 = smartSubmitCloudletList(brokerFourHosts4VM, filterCloudlet1 ,submittedList2, datacenterFourHosts4Vm)
        val filterCloudlet3 = smartSubmitCloudletList(brokerFourHosts8VM, filterCloudlet2, submittedList3 , datacenterFourHosts8Vm)

        if(filterCloudlet3.size != 0)
        {
          brokerFourHosts8VM.submitCloudletList(filterCloudlet3.asJava)
        }
        logger.info("Submission complete")

        //Assigning vm's to cloudlets
        logger.info("Assigning vm's to cloudlets")
        val fullList =  filterCloudlet3 ++ submittedList3.toList
        assignVM(brokerFourHosts4VM, submittedList2.toList, List4VM)
        assignVM(brokerFourHosts8VM, fullList, List8VM)
        assignVM(brokerTwoHosts2VM, submittedList1.toList, List2VM)
        logger.info("Assignment complete")

      }

    //Creation of the network
    logger.info("Building network")
    NetworkTopology.buildNetworkTopology("topology.brite")
    NetworkTopology.mapNode(datacenterFourHosts4Vm.getId, 0)
    NetworkTopology.mapNode(datacenterFourHosts8Vm.getId, 1)
    NetworkTopology.mapNode(datacenterTwoHosts2Vm.getId, 2)
    NetworkTopology.mapNode(brokerFourHosts4VM.getId, 3)
    NetworkTopology.mapNode(brokerFourHosts8VM.getId, 4)
    NetworkTopology.mapNode(brokerTwoHosts2VM.getId, 5)
    logger.info("Network complete")

    //**********Starting the cloud simulation************//
    CloudSim.startSimulation()


    Log.printLine()
    Log.printLine("========== OUTPUT ==========")
    Log.printLine("Cloudlet ID\t\tSTATUS\t\tData center ID\t\tVM ID\tTime\tStart Time\tFinish Time")

    //Writing to a txt file for ease of use
    writeFile(outputName,"========== OUTPUT ==========\n", false)
    writeFile(outputName, "Cloudlet ID\tData center ID\tVM ID\tTime\tStart Time\tFinish Time\n", true)

    val newList1 = brokerFourHosts4VM.getCloudletReceivedList().asScala
    val newList2 = brokerFourHosts8VM.getCloudletReceivedList().asScala
    val newList3 = brokerTwoHosts2VM.getCloudletReceivedList().asScala

    printCloudletList(newList1.toList,outputName)
    printCloudletList(newList2.toList,outputName)
    printCloudletList(newList3.toList,outputName)

    CloudSim.stopSimulation()
    //*********End of simulation***********//
  }

  @tailrec final def recMapReduce(  startingNumber: Int, target:Long, currentMips:Long, exponentCount:Int, numVM:Int, model: UtilizationModelFull): List[Cloudlet] =
  {
    //Method will call itself recursively
    if(currentMips <= target)
      {
        //Creating a list of cloudlets based on how many recursion calls and target mips
        var list = new ListBuffer[Cloudlet]
        createCloudlets(startingNumber,scala.math.pow(numVM,exponentCount).asInstanceOf[Int],currentMips.asInstanceOf[Int],list, model)
        list.toList
      }
    else
      {
        //Recursively call the method and update the values
        recMapReduce(startingNumber, target, currentMips / numVM, exponentCount + 1, numVM, model)
      }
  }

  def mapReduceCloudlet(startingNumber:Int, VMs : List[Vm] , mips:Long, model: UtilizationModelFull): List[Cloudlet] =
  {
    //Setting up values that will be passed to the recursive map reduce function
    val vm = VMs.apply(0)
    val vmMips = vm.getMips
    val numVm = VMs.size
    val target = vmMips.asInstanceOf[Long] / 9

    //Calling the recursive mapReduce function
    recMapReduce(startingNumber, target, mips,0, numVm,model)
  }

  @tailrec final def recSmartSubmitCloudletList(broker: DatacenterBroker, cloudletList: List[Cloudlet], submitList: ListBuffer[Cloudlet],returnList: List[Cloudlet], mips: Int): List[Cloudlet] = cloudletList match 
  {
    //If the list is empty then we can return the list of remaining cloudlets to look at
    case Nil => broker.submitCloudletList( submitList.asJava)
                returnList
    //when there are still items in the cloudlet then we check to see if the ratio is correct
    case e::rest => val ratio = (mips.asInstanceOf[Double] / e.getCloudletLength.asInstanceOf[Double])
                    if (ratio <= 10.0)
                    {
                      //If the ratio is correct then we can submit the list
                      submitList += e
                      recSmartSubmitCloudletList(broker, rest, submitList, returnList,mips)
                    }
                    else
                    {
                      //If the ratio is off then we will return the element to be checked in the next vmList
                      val newReturnList = e::returnList
                      recSmartSubmitCloudletList(broker,rest,submitList, newReturnList, mips)
                    }

  }
  def smartSubmitCloudletList(broker: DatacenterBroker, cloudletList: List[Cloudlet],bufferList: ListBuffer[Cloudlet], dataCenter: Datacenter): List[Cloudlet] =
    {
      //Since we are testing three datacenters each one will have its own configuration
      if(dataCenter.getName == "4VM")
        recSmartSubmitCloudletList(broker, cloudletList, bufferList, Nil, 1000)
      else if(dataCenter.getName == "8VM")
        recSmartSubmitCloudletList(broker, cloudletList, bufferList, Nil, 500 )
      else if(dataCenter.getName == "2VM")
        recSmartSubmitCloudletList(broker, cloudletList, bufferList, Nil, 2000)
      else
        Nil
    }
  @tailrec final def basicAssignVMRec(broker: DatacenterBroker, cloudletList: List[Cloudlet], ListVM: List[Vm], numVM: Int, numVMCurrent: Int): Unit = cloudletList match {
    //Once there are no more cloudlets to process then return
    //Otherwise this is a simple algorithm that assigns Tasks to VM's in no particular order
    case Nil =>
    case e :: rest => e.setUserId(broker.getId())
      broker.bindCloudletToVm(e.getCloudletId(), ListVM(numVMCurrent % numVM).getId())
      basicAssignVMRec(broker, rest, ListVM, numVM, numVMCurrent + 1)
  }

  def assignVM(broker: DatacenterBroker, cloudletList: List[Cloudlet], ListVM: List[Vm]): Unit = cloudletList match {
    //Calls basicAssignVMRec with extra parameters to make recursion possible
    //The basic assign just assigns the VM's to every task in no particular order
    case Nil => logger.error("No VM's to assign")
    case e::rest => basicAssignVMRec(broker, cloudletList, ListVM, ListVM.size, 0)


  }

  def createBroker(name: String): DatacenterBroker = {
    //Simple creation of a broker by passing the name to the constructor
    var broker = new DatacenterBroker(name)
    broker
  }

  def createCloudletType(option: Int, number: Int, model: UtilizationModelFull): Cloudlet = {

    if (option == 1) //50000 Mips
    {
      new Cloudlet(number, 50000, 1, 500, 600, model, model, model)

    }
    else if(option == 2) //100 Mips
    {
      new Cloudlet(number, 100, 1, 500,600, model, model, model)
    }
    else if(option == 3) //50 Mips
    {
      new Cloudlet(number, 50, 1, 500, 600, model,model,model)
    }
    else if(option == 4) //200 Mips
    {
      new Cloudlet(number, 200, 1, 500, 600, model,model,model)
    }
    else if(option == 5) //Random number of mips
    {
      val randomNum = scala.util.Random
      val workload = randomNum.nextInt(200)
      new Cloudlet(number, workload, 1, 500, 600, model,model,model)
    }
      else if(option >= 6) //Custom number of mips
      {
        new Cloudlet(number,option, 1,500,600,model,model,model)
      }
    else //The option is not available so we will just resort to this default
    {
      logger.error("CLOUDLET TYPE INCORRECT")
      logger.info("Building default cloudlet")
      new Cloudlet(number, 1000, 1, 500, 600, model, model, model)
    }



  }

  @tailrec final def createCloudlet(startingNumber: Int, taskType: Int, numberOfTasks: Int, finalList: ListBuffer[Cloudlet], model: UtilizationModelFull): ListBuffer[Cloudlet] = {
    //If the number of tasks is 0 (all tasks have been created) then return the final list, otherwise keep creating cloudlets
    if (numberOfTasks == 0)
      finalList
    else 
    {
      //Adding cloudlets to the final list by calling the create method
      finalList += createCloudletType(taskType, numberOfTasks + startingNumber, model)
      createCloudlet(startingNumber, taskType, numberOfTasks - 1, finalList, model)
    }

  }

  def createCloudlets(startingNumber: Int, numberOfTasks: Int, taskType: Int, emptyList: ListBuffer[Cloudlet], model: UtilizationModelFull): Unit = {

    //Error if there are no cloudlets
    if (numberOfTasks < 1)
    {
      logger.error("No specified cloudlet value, cloudlets will not be created")
    }

    else 
    {
      //Creating a cloudlet with specified parameters
      createCloudlet(startingNumber, taskType, numberOfTasks, emptyList, model)
    }

  }

  def createProcessors(processorType: Int): ListBuffer[Pe] = {

  
    var peList = new ListBuffer[Pe]()
    if (processorType == 1) {
      //4 cores that are 5000 Mips
      peList += new Pe(0, new PeProvisionerSimple(5000))
      peList += new Pe(1, new PeProvisionerSimple(5000))
      peList += new Pe(2, new PeProvisionerSimple(5000))
      peList += new Pe(3, new PeProvisionerSimple(5000))
      peList
    }
    
    else if (processorType == 2) {
      //4 cores that are 1000 Mips
      logger.info("Creating 4 1000mip cores")
      peList += new Pe(0, new PeProvisionerSimple(1000))
      peList += new Pe(1, new PeProvisionerSimple(1000))
      peList += new Pe(2, new PeProvisionerSimple(1000))
      peList += new Pe(3, new PeProvisionerSimple(1000))
      peList
    }
    else if (processorType == 3) {
      //8 cores that are 500 mips
      logger.info("Creating 8 500mip cores")
      peList += new Pe(0, new PeProvisionerSimple(500))
      peList += new Pe(1, new PeProvisionerSimple(500))
      peList += new Pe(2, new PeProvisionerSimple(500))
      peList += new Pe(3, new PeProvisionerSimple(500))
      peList += new Pe(4, new PeProvisionerSimple(500))
      peList += new Pe(5, new PeProvisionerSimple(500))
      peList += new Pe(6, new PeProvisionerSimple(500))
      peList += new Pe(7, new PeProvisionerSimple(500))
      peList
    }
    else if (processorType == 4) {
      //2 cores that are 2000mips
      logger.info("Creating 2 2000mip cores")
      peList += new Pe(0, new PeProvisionerSimple(2000))
      peList += new Pe(1, new PeProvisionerSimple(2000))
      peList
    }
    else {
      logger.error("No processor type selected, no processors will be made")
      peList
    }


  }

  @tailrec final def createHosts(hostList: List[Host], numHosts: Int, systemType: Int): List[Host] = {
    //If there are no hosts to create then return an empty list, otherwise begin creating the systems
    if (numHosts < 1) 
    {
      hostList
    }
    else 
    {

      //First we need to create a list of processors for our systems
      var peList = new ListBuffer[Pe]()
      peList = createProcessors(systemType)

      //Cloudsim functions require Java style Lists so I need to make the conversions
      //These are also all of the characteristics to create a system
      val newList = new Host(
        numHosts,
        new RamProvisionerSimple(8126),
        new BwProvisionerSimple(8000),
        100000,
        peList.asJava,
        new VmSchedulerSpaceShared(peList.asJava)) :: hostList
      createHosts(newList, numHosts - 1, systemType)
    }

  }

  def createDataCenter(name: String, datacenterType: Int, numberOfHosts: Int): Datacenter = 
  {
    //Creating a datacenter, first we need to create the host computers
    val hostList = createHosts(Nil, numberOfHosts, datacenterType)
    val storageList = new util.LinkedList[Storage]
    val characteristics = new DatacenterCharacteristics("x86", "Linux", "Xen", hostList.asJava, 10.0, 3, 0.05, 0.001, 0.0)
    var datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList.asJava), storageList, 0)
    datacenter
  }

  def createVM(startingNumber:Int, num: Int, taskType: Int, brokerID: Int, finalList: List[Vm]): List[Vm] = {
    //Once there are no more vm's to create the return the final list
    if (num == 0) 
    {
      finalList
    }
    else
     {

      if (taskType == 1) //5000Mip VMs
      {
        logger.info("Creating list type 1")
        var newList = new Vm(num+startingNumber, brokerID, 5000, 1, 1, 1, 20000, "Xen", new CloudletSchedulerSpaceShared) :: finalList
        createVM(startingNumber, num - 1, taskType, brokerID, newList)
      }
      else if (taskType == 2)//1000Mip VMs
      {
        var newList = new Vm(num+startingNumber, brokerID, 1000, 1, 1024, 1000, 20000, "Xen", new CloudletSchedulerSpaceShared) :: finalList
        createVM(startingNumber, num - 1, taskType, brokerID, newList)
      }
      else if (taskType == 3)//500Mip VMs
      {
        var newList = new Vm(num+startingNumber, brokerID, 500, 1, 1024, 1000, 20000, "Xen", new CloudletSchedulerSpaceShared) :: finalList
        createVM(startingNumber,num- 1, taskType, brokerID, newList)
      }
      else if (taskType == 4)//2000Mip VMs
      {
        var newList = new Vm(num+startingNumber, brokerID, 2000, 1, 1024, 1000, 20000, "Xen", new CloudletSchedulerSpaceShared) :: finalList
        createVM(startingNumber,num - 1, taskType, brokerID, newList)
      }
      else
      {
        //The type does not exist
        logger.error("Incorrect VM Type")
        Nil
      }


    }
  }

  def createVMs(startingNumber: Int, numberOf: Int, taskType: Int, brokerID: Int): List[Vm] = {
    //If there are no number of VM's to create then return an empty list, otherwise we will begin the recursive statement
    if (numberOf == 0) {
      logger.error("No number of VM's specified, none will be created")
      Nil
    }
    else {
      //Once the list is created we will return the list of virtual machines
      val listVM = createVM(startingNumber, numberOf, taskType, brokerID, Nil)
      listVM
    }
  }

  @tailrec final def printCloudletList(list: List[Cloudlet], fileName:String): Unit = list match {
    //When there is not more cloudlets to print then return otherwise print all of the information
    case Nil =>
    case e :: rest => val dft = new DecimalFormat("###.##") //This just prints everything to the terminal and also writes to a file
      Log.print(e.getCloudletId() + "\t\t\t\t")
      writeFile(fileName, e.getCloudletId() + "\t\t\t\t" , true)
      if (e.getStatus() == Cloudlet.SUCCESS) {
        
        //Printing to the screen
        Log.print("SUCCESS\t\t")
        Log.printLine(e.getResourceId + "\t\t\t\t\t" + e.getVmId + "\t\t" + dft.format(e.getActualCPUTime) + "\t\t" +
          dft.format(e.getExecStartTime) + "\t\t\t" + dft.format(e.getFinishTime))

        //Grabbing the string we want to get so we could write to the file
        val strOutput = e.getResourceId.toString + "\t\t" + e.getVmId.toString + "\t\t" + dft.format(e.getActualCPUTime).toString + "\t\t" +
          dft.format(e.getExecStartTime).toString + "\t\t" + dft.format(e.getFinishTime).toString + "\n"
        writeFile(fileName, strOutput,true)
      }
      printCloudletList(rest,fileName)


  }

  def writeFile(filename: String, inputStr: String, append: Boolean): Unit =
  {
    //Creating file with filename
    //append will determine if we need to append or start a new file
    val file = new File(filename)
    val bw = new BufferedWriter(new FileWriter(file,append))
    bw.write(inputStr)
    bw.close()
  }
}


