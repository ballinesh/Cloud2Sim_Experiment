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

class createBrokerTest extends FlatSpec with Matchers {
  val sim = new simulator

  val num_user = 1 // number of cloud users
  val calendar: Calendar = Calendar.getInstance // Calendar whose fields have been initialized with the current date and time.
  val trace_flag = false // trace events

  //initializing the cloudsim
  CloudSim.init(num_user, calendar, trace_flag)
  val model = new UtilizationModelFull()
  val broker = sim.createBroker("BrokerName")
  val broker2 = sim.createBroker("Broker2")

  assert(broker.getName() == "BrokerName")
  assert(broker2.getName() == "Broker2")
  assert(broker.getId != broker2.getId)
}