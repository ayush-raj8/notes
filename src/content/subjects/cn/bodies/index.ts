import type { LessonBody } from "../../../schema";
import { body as m01Encapsulation } from "../modules/m01/encapsulation";
import { body as m01Layering } from "../modules/m01/layering";
import { body as m01PlanesAndConnections } from "../modules/m01/planes-and-connections";
import { body as m01WhatANetworkMustDo } from "../modules/m01/what-a-network-must-do";
import { body as m02Arp } from "../modules/m02/arp";
import { body as m02EthernetAndMac } from "../modules/m02/ethernet-and-mac";
import { body as m02Ipv6Nd } from "../modules/m02/ipv6-neighbor-discovery";
import { body as m02SameSubnet } from "../modules/m02/same-subnet";
import { body as m02Switching } from "../modules/m02/switching";
import { body as m03GatewayAndLpm } from "../modules/m03/gateway-and-longest-prefix";
import { body as m03Ipv4Cidr } from "../modules/m03/ipv4-and-cidr";
import { body as m03Ipv6 } from "../modules/m03/ipv6-addressing";
import { body as m03SpecialAddresses } from "../modules/m03/special-addresses";
import { body as m03TtlAndFragmentation } from "../modules/m03/ttl-and-fragmentation";
import { body as m04Icmp } from "../modules/m04/icmp";
import { body as m04OspfBgp } from "../modules/m04/ospf-and-bgp";
import { body as m04Algorithms } from "../modules/m04/routing-algorithms";
import { body as m04RoutingVsForwarding } from "../modules/m04/routing-vs-forwarding";
import { body as m05Congestion } from "../modules/m05/congestion-control";
import { body as m05FlowControl } from "../modules/m05/flow-control";
import { body as m05Handshake } from "../modules/m05/handshake";
import { body as m05Reliability } from "../modules/m05/reliability";
import { body as m05HeaderAndNumbers } from "../modules/m05/sequence-numbers";
import { body as m05ServiceModel } from "../modules/m05/service-model";
import { body as m05Interactions } from "../modules/m05/tcp-interactions";
import { body as m05Termination } from "../modules/m05/termination";
import { body as m06QuicAndTradeoffs } from "../modules/m06/quic-and-tradeoffs";
import { body as m06UdpService } from "../modules/m06/udp-service";
import { body as m06UseCases } from "../modules/m06/use-cases";
import { body as m07Caching } from "../modules/m07/caching";
import { body as m07Hierarchy } from "../modules/m07/hierarchy";
import { body as m07Records } from "../modules/m07/records";
import { body as m07Resolution } from "../modules/m07/resolution";
import { body as m07TransportAndFailures } from "../modules/m07/transport-and-failures";
import { body as m08HttpVersions } from "../modules/m08/http-versions";
import { body as m08Intermediaries } from "../modules/m08/intermediaries";
import { body as m08RequestResponse } from "../modules/m08/request-response";
import { body as m08ReuseAndCache } from "../modules/m08/reuse-and-cache";
import { body as m09Certificates } from "../modules/m09/certificates";
import { body as m09Handshake } from "../modules/m09/handshake";
import { body as m09Limits } from "../modules/m09/limits";
import { body as m09WhyTls } from "../modules/m09/why-tls";
import { body as m10ConnectivityFailures } from "../modules/m10/connectivity-failures";
import { body as m10Firewalls } from "../modules/m10/firewalls";
import { body as m10LoadBalancing } from "../modules/m10/load-balancing";
import { body as m10Nat } from "../modules/m10/nat";

/**
 * Lesson bodies keyed by lesson id.
 * A missing key means the lesson is still planned.
 * Register a body here when its file exists. Do not duplicate a lesson in a second file.
 */
export const lessonBodies: Record<string, LessonBody> = {
  [m01WhatANetworkMustDo.id]: m01WhatANetworkMustDo,
  [m01Layering.id]: m01Layering,
  [m01Encapsulation.id]: m01Encapsulation,
  [m01PlanesAndConnections.id]: m01PlanesAndConnections,
  [m02EthernetAndMac.id]: m02EthernetAndMac,
  [m02Switching.id]: m02Switching,
  [m02Arp.id]: m02Arp,
  [m02SameSubnet.id]: m02SameSubnet,
  [m02Ipv6Nd.id]: m02Ipv6Nd,
  [m03Ipv4Cidr.id]: m03Ipv4Cidr,
  [m03SpecialAddresses.id]: m03SpecialAddresses,
  [m03GatewayAndLpm.id]: m03GatewayAndLpm,
  [m03Ipv6.id]: m03Ipv6,
  [m03TtlAndFragmentation.id]: m03TtlAndFragmentation,
  [m04RoutingVsForwarding.id]: m04RoutingVsForwarding,
  [m04Algorithms.id]: m04Algorithms,
  [m04OspfBgp.id]: m04OspfBgp,
  [m04Icmp.id]: m04Icmp,
  [m05ServiceModel.id]: m05ServiceModel,
  [m05HeaderAndNumbers.id]: m05HeaderAndNumbers,
  [m05Handshake.id]: m05Handshake,
  [m05Reliability.id]: m05Reliability,
  [m05FlowControl.id]: m05FlowControl,
  [m05Congestion.id]: m05Congestion,
  [m05Interactions.id]: m05Interactions,
  [m05Termination.id]: m05Termination,
  [m06UdpService.id]: m06UdpService,
  [m06UseCases.id]: m06UseCases,
  [m06QuicAndTradeoffs.id]: m06QuicAndTradeoffs,
  [m07Hierarchy.id]: m07Hierarchy,
  [m07Resolution.id]: m07Resolution,
  [m07Records.id]: m07Records,
  [m07Caching.id]: m07Caching,
  [m07TransportAndFailures.id]: m07TransportAndFailures,
  [m08RequestResponse.id]: m08RequestResponse,
  [m08HttpVersions.id]: m08HttpVersions,
  [m08ReuseAndCache.id]: m08ReuseAndCache,
  [m08Intermediaries.id]: m08Intermediaries,
  [m09WhyTls.id]: m09WhyTls,
  [m09Certificates.id]: m09Certificates,
  [m09Handshake.id]: m09Handshake,
  [m09Limits.id]: m09Limits,
  [m10Nat.id]: m10Nat,
  [m10Firewalls.id]: m10Firewalls,
  [m10LoadBalancing.id]: m10LoadBalancing,
  [m10ConnectivityFailures.id]: m10ConnectivityFailures,
};
