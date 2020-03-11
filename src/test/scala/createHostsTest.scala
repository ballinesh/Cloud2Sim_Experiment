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

class createHostsTest  extends FlatSpec with Matchers {

    val sim = new simulator

    val num_user = 1 // number of cloud users
    val calendar: Calendar = Calendar.getInstance // Calendar whose fields have been initialized with the current date and time.
    val trace_flag = false // trace events

    //initializing the cloudsim
    CloudSim.init(num_user, calendar, trace_flag)
    val broker1 = sim.createBroker("BrokerName")

    val hostList1 = sim.createHosts(Nil , 3, 1)
    val hostList2 = sim.createHosts(Nil,3,2 )
    val hostList3 = sim.createHosts(Nil,3,3 )
    val hostList4 = sim.createHosts(Nil,  3, 4)

    assert(hostList1.length == 3)
    assert(hostList2.length == 3)
    assert(hostList3.length == 3)
    assert(hostList4.length == 3)

    val peList1 = sim.createProcessors(1)
    val peList2 = sim.createProcessors(2)
    val peList3 = sim.createProcessors(3)
    val peList4 = sim.createProcessors(4)

    hostMipTest()
    hostPETest()
    processorTest()


  def hostMipTest(): Unit = {

    assert(hostList1.apply(0).getAvailableMips == 20000)
    assert(hostList1.apply(1).getAvailableMips == 20000)
    assert(hostList1.apply(2).getAvailableMips == 20000)

    assert(hostList2.apply(0).getAvailableMips == 4000)
    assert(hostList2.apply(1).getAvailableMips == 4000)
    assert(hostList2.apply(2).getAvailableMips == 4000)

    assert(hostList3.apply(0).getAvailableMips == 4000)
    assert(hostList3.apply(1).getAvailableMips == 4000)
    assert(hostList3.apply(2).getAvailableMips == 4000)

    assert(hostList4.apply(0).getAvailableMips == 4000)
    assert(hostList4.apply(1).getAvailableMips == 4000)
    assert(hostList4.apply(2).getAvailableMips == 4000)
  }

  def hostPETest(): Unit ={

    assert(hostList1.apply(0).getNumberOfPes == 4)
    assert(hostList1.apply(1).getNumberOfPes == 4)
    assert(hostList1.apply(2).getNumberOfPes == 4)

    assert(hostList2.apply(0).getNumberOfPes == 4)
    assert(hostList2.apply(1).getNumberOfPes == 4)
    assert(hostList2.apply(2).getNumberOfPes == 4)

    assert(hostList3.apply(0).getNumberOfPes == 8)
    assert(hostList3.apply(1).getNumberOfPes == 8)
    assert(hostList3.apply(2).getNumberOfPes == 8)

    assert(hostList4.apply(0).getNumberOfPes == 2)
    assert(hostList4.apply(1).getNumberOfPes == 2)
    assert(hostList4.apply(2).getNumberOfPes == 2)

  }

  def processorTest(): Unit = {

    assert(peList1.length == 4)
    assert(peList2.length == 4)
    assert(peList3.length == 8)
    assert(peList4.length == 2)

    assert(peList1.apply(0).getMips == 5000)
    assert(peList1.apply(1).getMips == 5000)
    assert(peList1.apply(2).getMips == 5000)
    assert(peList1.apply(3).getMips == 5000)

    assert(peList2.apply(0).getMips == 1000)
    assert(peList2.apply(1).getMips == 1000)
    assert(peList2.apply(2).getMips == 1000)
    assert(peList2.apply(3).getMips == 1000)

    assert(peList3.apply(0).getMips == 500)
    assert(peList3.apply(1).getMips == 500)
    assert(peList3.apply(2).getMips == 500)
    assert(peList3.apply(3).getMips == 500)
    assert(peList3.apply(4).getMips == 500)
    assert(peList3.apply(5).getMips == 500)
    assert(peList3.apply(6).getMips == 500)
    assert(peList3.apply(7).getMips == 500)

    assert(peList4.apply(0).getMips == 2000)
    assert(peList4.apply(1).getMips == 2000)




  }


}
