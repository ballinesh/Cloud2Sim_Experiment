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

class CreateVmTest extends FlatSpec with Matchers {
  val sim = new simulator

  val num_user = 1 // number of cloud users
  val calendar: Calendar = Calendar.getInstance // Calendar whose fields have been initialized with the current date and time.
  val trace_flag = false // trace events

  //initializing the cloudsim
  CloudSim.init(num_user, calendar, trace_flag)
  val broker = sim.createBroker("Broker")

  val vmList1 = sim.createVMs(0, 3, 1, broker.getId)
  val vmList2 = sim.createVMs(10, 3, 2, broker.getId)
  val vmList3 = sim.createVMs(20, 3, 3,broker.getId)
  val vmList4 = sim.createVMs(30,3, 4,broker.getId)

  assert(vmList1.length == 3)
  assert(vmList2.length == 3)
  assert(vmList3.length == 3)
  assert(vmList4.length == 3)

  testMips()
  testId()

  def testMips (): Unit = {

    assert(vmList1.apply(0).getMips == 5000 )
    assert(vmList1.apply(1).getMips == 5000 )
    assert(vmList1.apply(2).getMips == 5000 )

    assert(vmList2.apply(0).getMips == 1000 )
    assert(vmList2.apply(1).getMips == 1000 )
    assert(vmList2.apply(2).getMips == 1000 )

    assert(vmList3.apply(0).getMips == 500 )
    assert(vmList3.apply(1).getMips == 500 )
    assert(vmList3.apply(2).getMips == 500 )

    assert(vmList4.apply(0).getMips == 2000 )
    assert(vmList4.apply(1).getMips == 2000 )
    assert(vmList4.apply(2).getMips == 2000 )
  }

  def testId(): Unit = {
    assert(vmList1.apply(0).getId == 1 )
    assert(vmList1.apply(1).getId == 2 )
    assert(vmList1.apply(2).getId == 3 )

    assert(vmList2.apply(0).getId == 11 )
    assert(vmList2.apply(1).getId == 12 )
    assert(vmList2.apply(2).getId == 13 )

    assert(vmList3.apply(0).getId == 21 )
    assert(vmList3.apply(1).getId == 22 )
    assert(vmList3.apply(2).getId == 23 )

    assert(vmList4.apply(0).getId == 31 )
    assert(vmList4.apply(1).getId == 32 )
    assert(vmList4.apply(2).getId == 33 )
  }



}
