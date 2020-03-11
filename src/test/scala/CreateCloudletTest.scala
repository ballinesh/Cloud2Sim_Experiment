import org.scalatest._
import org.scalatest.FlatSpec
import java.io.{BufferedWriter, File, FileWriter}
import java.text.DecimalFormat
import java.util
import java.util.{Calendar, LinkedList}

import scala.collection.mutable.ListBuffer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.cloudbus.cloudsim.{Cloudlet, CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared, Datacenter, DatacenterBroker, DatacenterCharacteristics, Host, Log, Pe, Storage, UtilizationModel, UtilizationModelFull, Vm, VmAllocationPolicySimple, VmSchedulerSpaceShared, VmSchedulerTimeShared}
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

class CreateCloudletTest extends FlatSpec with Matchers {
  val sim = new simulator

  val num_user = 1 // number of cloud users
  val calendar: Calendar = Calendar.getInstance // Calendar whose fields have been initialized with the current date and time.
  val trace_flag = false // trace events

  //initializing the cloudsim
  CloudSim.init(num_user, calendar, trace_flag)
  val model = new UtilizationModelFull()

  val cloudletList1 = new ListBuffer[Cloudlet]
  val cloudletList2 = new ListBuffer[Cloudlet]
  val cloudletList3 = new ListBuffer[Cloudlet]
  val cloudletList4 = new ListBuffer[Cloudlet]

  //Assigning values to all of the cloudlet Lists/ cloudlets
  val cloudletType1 = sim.createCloudletType(1, 0, model)
  val cloudletType2 = sim.createCloudletType(2, 1, model)
  val cloudletType1List = sim.createCloudlet(0, 1, 5, cloudletList1, model)
  val cloudletType2List = sim.createCloudlet(0, 2, 5, cloudletList2, model)
  sim.createCloudlets(0, 3, 1, cloudletList3, model)
  sim.createCloudlets(0, 3, 2, cloudletList4, model)

  runCreateCloudletsTest()
  runCreateCloudletTest()
  runCreateCloudletTypeTest()

  def runCreateCloudletsTest(): Unit = {
    assert(cloudletList3.length == 3)
    assert(cloudletList3.apply(0).getCloudletLength == 50000)
    assert(cloudletList3.apply(1).getCloudletLength == 50000)
    assert(cloudletList3.apply(2).getCloudletLength == 50000)

    assert(cloudletList4.length == 3)
    assert(cloudletList4.apply(0).getCloudletLength == 100)
    assert(cloudletList4.apply(1).getCloudletLength == 100)
    assert(cloudletList4.apply(2).getCloudletLength == 100)
  }

  def runCreateCloudletTest(): Unit = {
    assert(cloudletList1.length == 5)
    assert(cloudletList2.length == 5)

    assert(cloudletList1.apply(0).getCloudletLength == 50000)
    assert(cloudletList1.apply(1).getCloudletLength == 50000)
    assert(cloudletList1.apply(2).getCloudletLength == 50000)
    assert(cloudletList1.apply(3).getCloudletLength == 50000)
    assert(cloudletList1.apply(4).getCloudletLength == 50000)

    assert(cloudletList2.apply(0).getCloudletLength == 100)
    assert(cloudletList2.apply(1).getCloudletLength == 100)
    assert(cloudletList2.apply(2).getCloudletLength == 100)
    assert(cloudletList2.apply(3).getCloudletLength == 100)
    assert(cloudletList2.apply(4).getCloudletLength == 100)
  }

  def runCreateCloudletTypeTest(): Unit = {
    assert(cloudletType1.getCloudletLength == 50000)
    assert(cloudletType2.getCloudletLength == 100)
  }









}
