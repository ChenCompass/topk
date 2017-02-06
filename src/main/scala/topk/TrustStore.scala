package topk

import java.nio.file.{Files, Paths}
import java.security.KeyStore
import java.security.cert.CertificateFactory

object TrustStore {
  def create(): Unit = {
    val trustStorePath = Paths.get("letsEncrypt.jks")
    if (!trustStorePath.toFile.exists()){
      val ks = KeyStore.getInstance(KeyStore.getDefaultType)
      ks.load(null, "changeit".toCharArray)
      val cf = CertificateFactory.getInstance("X.509")
      val root = cf.generateCertificate(Files.newInputStream(Paths.get("dst_root_ca_x3.cer")))
      val letsEncrypt = cf.generateCertificate(Files.newInputStream(Paths.get("lets-encrypt-x3-cross-signed.cer")))

      ks.setCertificateEntry("DST Root CA 3", root)
      ks.setCertificateEntry("Let's Encrypt Authority X3", letsEncrypt)
      ks.store(Files.newOutputStream(trustStorePath), "changeit".toCharArray)
    }
  }

  def load(): Unit = {
    System.setProperty("javax.net.ssl.trustStore", "letsEncrypt.jks")
  }

}
