import java.io as io
import org.python.util as util
import atg.cim.productconfig.deploy.websphere.WebsphereTopology as Topology
import atg.cim.productconfig.deploy.websphere.WebsphereTopology.WebSphereNode as Node
import atg.cim.productconfig.deploy.websphere.WebsphereTopology.WebSphereServer as Server

#A simple parenthesis is not enough, as Node/Server ids can contain them.
SPLIT_STRING = '(cells/'

# Return the "containment id" from an AdminConfig.list reference.
def firstSplit (stringToSplice):
  return stringToSplice.split(SPLIT_STRING)[0]

topology = Topology.getInstance()

cellId = firstSplit(AdminConfig.list('Cell').replace('\r', ''))

for nodeId in AdminConfig.list('Node').split('\n'):
  tNodeId = nodeId.replace('\r', '')
  node = topology.createNode(firstSplit(tNodeId))
  for serverId in AdminConfig.list('Server', tNodeId).split('\n'):
    tServerId = serverId.replace('\r', '')
    server = topology.createServer(firstSplit(tServerId))
    applicationPath = "WebSphere:cell="+cellId+",node="+firstSplit(tNodeId)+",server="+firstSplit(tServerId)
    for applicationId in AdminApp.list(applicationPath).split('\n'):
      server.addApplication(applicationId.replace('\r',''))
    node.addServer(server)
  topology.addNode(node)

outFile = io.FileOutputStream("websphere.environment")
outStream = io.ObjectOutputStream(outFile)
outStream.writeObject(topology)
outFile.close()
