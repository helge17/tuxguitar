#! /usr/bin/make -f
# -*- makefile -*-
#ident "$Id: Makefile,v 1.19 2008/04/17 20:06:22 rzr Exp $"
#@author: created by www.philippe.coval.online.fr -- revision: $Author: rzr $
#licence: LGPL
#------------------------------------------------------------------------------

default: debsrc

package?=tuxguitar
package?=${PACKAGE}


snapshot-default: snapshot-deb


#~rzr:{ various debian hack to be removed

version=$(shell date +%Y%m%d)
#debversion?=$(shell dpkg-parsechangelog | sed -n 's/Version: \(.*\)/\1/p')
scmroot?=https://${package}.svn.sourceforge.net/svnroot/${package}/trunk
#distfile?=${package}_${debversion}~snapshot$(shell date +%Y%m%d).orig.tar.gz

pkg_ver?=${version_next}~snapshot${version}
pkg_dir?=${package}-${pkg_ver}

#{
snapshot_version?=0.0.${version}
snapshot_dir?=${package}-snapshot-${snapshot_version}


snapshot: ../${snapshot_dir} 

../${snapshot_dir}:
	svn co ${scmroot} $@
	cd .. && tar czf ${snapshot_dir}.tar.gz --exclude="debian" ${snapshot_dir}
	cd ../${snapshot_dir} && find . > 00index.txt
	cd .. && ln -fs ${snapshot_dir}.tar.gz ${package}-snapshot_${snapshot_version}.orig.tar.gz 
	cd .. && ln -fs ${snapshot_dir} ${package}-snapshot
	echo "# cd ../${snapshot_dir}"


fix-release: overide
	-cp TuxGuitar/scripts/tuxguitar.tg  ./
	-cp TuxGuitar/scripts/tuxguitar.xpm  ./
	-cp TuxGuitar/scripts/tuxguitar.gp3  ./

clean/snapshot:
	rm -f *.tg *.gp3
#}

refresh:
	 ls | grep TuxGuitar- | xargs echo modules?= > tmp.mk

DEBPACKAGE_VERSION?=$(shell dpkg-parsechangelog  | sed -n 's/Version: \(.*\)/\1/p')
DEBPACKAGE?=$(shell dpkg-parsechangelog | sed -n 's/Source: \(.*\)/\1/p')


sync:
	${MAKE} deb # || make snapshot && cd ../${snapshot_dir} && ${MAKE} deb
	diffstat  tmp.diff


chk:
	ls ../${package}*${snapshot_version}*.changes || ls ../${package}*.changes
	lintian -i ../tuxguitar-snapshot*.changes



tarball_name=TuxGuitar-full-1.0-rc1-src

#	cd .. && ln -fs ${CURDIR} ${tarball_name} 

#to-upstream:
#	cp ./debian/${package}.sh TuxGuitar/scripts/
#	cp ./debian/${package}.sgml TuxGuitar/scripts/
#	cp ./debian/${package}.desktop TuxGuitar/scripts/


release: tarball
	mkdir -p ../tmp/${tarball_name} && cd ../tmp/${tarball_name} \
	 && tar xf ${CURDIR}/../${tarball_name}.tar.gz \
	 && cd ${tarball_name} && make build
	echo "../${tarball_name}.tar.gz"

cvsrelease:
	${MAKE} fetch
	${MAKE} -C ../${tarball_name} clean tarball
	ls -l ../${tarball_name}.tar.gz

fetch:
	mkdir -p ../${tarball_name}
	cd  ../${tarball_name} && \
	 cvs -d${scmroot} login  && \
	 cvs -d${scmroot} co .
	echo "cd ../${tarball_name}"

#test-help: ${CURDIR}
#	echo "$<"
#	echo "${<F}"
#	echo "${<N}"
#	echo "${<D}"

sf:
	lynx -dump "http://sourceforge.net/project/showfiles.php?group_id=155855&package_id=173684&release_id=580322" | grep filename | sed -e 's|.* http:.*filename=\(.*\)|http://heanet.dl.sourceforge.net/sourceforge/tuxguitar/\1|g' | xargs -n 1 wget -nc
	unp *-src.tar.gz
	rm *.tar.gz *.bin *.exe *.zip 
	for t in *-1.0-rc1-src ; do  echo mv $t ${t/-1.0-rc1-src/}; done

install_plugins: ${modules_plugins}
	install $</$<.jar ${INSTALL_SHARE_DIR}

#snapshot-test: ${CURDIR}
#	echo ${<:%-1.0.0=%}
#	echo ${<:%-1.0.0=%-snapshot}
#	echo ${<:%-%=%-snapshot}
#	echo ${<:%-1.0.0=%-snapshot}

install-libs: ${modules_jni}
	echo ${^:TuxGuitar-%=tuxguitar-%} 
	for t in ${^} ; do  \
	 echo install -s  $$t/$${t/TuxGuitar/tuxguitar} \
	 ; done
#	 echo install -s $$t/lib$(shell echo $${t} | tr [A-Z] [a-z])-jni.so  ${INSTALL_LIB_DIR}/ ; 
# TODO:
#	for t in ${modules_plugins} ; do cd "${CURDIR}/$$t" && \
#	 ant -v -d ${ANT_FLAGS} install ; \
#	done

tuxguitar-install:
	-cp -rfa ./TuxGuitar/share/files ${INSTALL_SHARE_DIR} # TODO
	-cp -rfa ./TuxGuitar/share/services ${INSTALL_SHARE_DIR} # TODO
	-cp -rfa ./TuxGuitar/share/themes ${INSTALL_SHARE_DIR} # TODO

CURDIR?=$(shell pwd)

#tarball:$(shell pwd)
#	find . -type l -exec file {} \; | sed -e "s|\(.*\): symbolic link to \`\(.*\)'|rm \1; cp -fa \2 \1|g"
#	find . -iname "*.jar"
#	rm ./README; cp -fa TuxGuitar/doc/README ./README
#	rm ./ChangeLog; cp -fa TuxGuitar/doc/CHANGES ./ChangeLog
#	rm ./COPYING; cp -fa TuxGuitar/doc/LICENSE ./COPYING
#	rm ./AUTHORS; cp -fa TuxGuitar/doc/AUTHORS ./AUTHORS
#	cd .. && tar cvfz  ${<F}.tar.gz \
##	 --exclude="debian" \
#	 --exclude "CVS" --exclude "CVSROOT" --exclude ".cvsignore" \
#	 --exclude="tmp.*" --exclude "build-stamp" \
#	 --exclude="changed*.diff"	 \
#	 ${<F}/*
#	ls -l ../${<F}.tar.gz
#	cd .. && ln -fs ${tarball_name}.tar.gz ${package}_${pkg_ver}.orig.tar.gz

debsrc: clean/snapshot update
	debuild && sudo debi && dpkg -L ${package}
	debuild -S | grep "_source.changes"

remove:
	-sudo aptitude -y remove ${package} ${package}-snapshot

install: remove
	debi || sudo debi


debsrc:  update remove
	dch -i # -a "WIP:"
	-fakeroot ./debian/rules clean
	-make clean 
	debuild -sa -S
	debrelease -S --dput ppa


deb: update remove
	dch -i # -a "WIP:"
	-fakeroot ./debian/rules clean
	-make clean 
	debuild -sa -S
	debuild -sa 
	sudo debi
	make clean
	debuild -sa -S
	debrelease -S --dput ppa
#	dput ppa  ../${DEBPACKAGE}*${DEBPACKAGE_VERSION}*source.changes

merge:
	-diff Makefile ../tuxguitar-snapshot-*/Makefile 
	-cp Makefile ../tuxguitar-snapshot-*/Makefile
	-diff debian/tuxguitar.sh ../tuxguitar-snapshot-*/TuxGuitar/scripts/tuxguitar.sh 
	-cp debian/tuxguitar.sh ../tuxguitar-snapshot-*/TuxGuitar/scripts/tuxguitar.sh 
	-cp /rzr.online.fr/docs/contribs/files/tuxguitar.tg ../tuxguitar-snapshot-*/TuxGuitar/scripts/
#	-rm ../tuxguitar-snapshot-*/TuxGuitar/scripts/tuxguitar.tg
	-cp tuxguitar.desktop  ../tuxguitar-snapshot-*/TuxGuitar/scripts/
	-cp tuxguitar.sgml  ../tuxguitar-snapshot-*/TuxGuitar/scripts/


apt:
	sudo aptitude install build-essential docbook-utils gcj libasound2-dev

update:
#	rm -f TuxGuitar/dist/config* TuxGuitar/build.*
#	-ssh-add
	-svn $@ .
	-svn diff > tmp.diff
	diffstat  tmp.diff

snapshot-clean:
	-rm -rf TuxGuitar/build* AUTHORS COPYING ChangeLog LICENSE README

commit: snapshot-clean clean distclean update help
	${HOME}/bin/vc.sh changed
	less tmp.diff
	svn commit

#~rzr:} various debian hack to be removed

#all: clean fix jar jni
help:
	@echo "${scmroot}"
	@echo "TODO: MOZILLA_FIVE_HOME, libc ver, plugins, jsa"
	echo "FIX: (LP: #176979) ; oef dist"
	@echo "TODO: timidity or fluidsynth installed ?"
	echo "/rzr.online.fr/docs/contribs/files/${PACKAGE}.mk"
	echo "org.herac.tuxguitar.gui.help.doc.DocException: org.eclipse.swt.SWTError: No more handles [Unknown Mozilla path (MOZILLA_FIVE_HOME not set)]"
	echo "<man-di> just In case someone wants to know: I starting commiting my eclipse 3.3 work"
	@echo "# $@"


r:
	sh /mnt/hda5/home/rzr/home/src/debian/tuxguitar/tuxguitar-0.9.99~1.0rc1/debian/tuxguitar.sh /rzr.online.fr/docs/contribs/files/tuxguitar.tg

deb-fix: TuxGuitar
	cp -a $</xml/build* $</
	cp -a $</doc/LICENSE COPYING
	cp -a $</doc/CHANGES ChangeLog
	cp -a $</doc/README ./
	cp -a $</doc/AUTHORS ./
	find . -iname "*.desktop"
	find . -iname "*.xpm"
	find . -iname "*.sgml"

#{ TODO:
clean-snapshot:
	rm -rf TuxGuitar/build* AUTHORS COPYING ChangeLog LICENSE README
#}
snapshot-run: ./TuxGuitar


snapshot-install:
	-cd debian &&  for t in  tuxguitar*.* ; do ln -fs $$t $${t/tuxguitar/tuxguitar-snapshot} ; done ;
	cd debian && ln -fs tuxguitar-alsa.install tuxguitar-snapshot-alsa.install
	cd debian && ln -fs tuxguitar-jsa.install tuxguitar-snapshot-jsa.install
	cd debian && ln -fs tuxguitar-oss.install tuxguitar-snapshot-oss.install
	cd debian && ln -fs tuxguitar.install tuxguitar-snapshot.install

	cd debian && ln -fs tuxguitar-snapshot-alsa tuxguitar-alsa
	cd debian && ln -fs tuxguitar-snapshot-oss tuxguitar-oss
	cd debian && ln -fs tuxguitar-snapshot tuxguitar


snapshot-deb: snapshot snapshot-clean update deb
	-rm -f [a-z]*

get-snapshot-source: ../${snapshot_dir}

build-gcj:
	${MAKE} build ANT_FLAGS="build.compiler=gcj" 

#eof "$Id:"
#! /usr/bin/make -f
# -*- makefile -*-
#ident "$Id: Makefile,v 1.19 2008/04/17 20:06:22 rzr Exp $"
#@author: created by www.philippe.coval.online.fr -- revision: $Author: rzr $
#licence: LGPL
#------------------------------------------------------------------------------

default: debsrc

package?=tuxguitar
package?=${PACKAGE}


snapshot-default: snapshot-deb


#~rzr:{ various debian hack to be removed

version=$(shell date +%Y%m%d)
#debversion?=$(shell dpkg-parsechangelog | sed -n 's/Version: \(.*\)/\1/p')
scmroot?=https://${package}.svn.sourceforge.net/svnroot/${package}/trunk
#distfile?=${package}_${debversion}~snapshot$(shell date +%Y%m%d).orig.tar.gz

pkg_ver?=${version_next}~snapshot${version}
pkg_dir?=${package}-${pkg_ver}

#{
snapshot_version?=0.0.${version}
snapshot_dir?=${package}-snapshot-${snapshot_version}


snapshot: ../${snapshot_dir} 

../${snapshot_dir}:
	svn co ${scmroot} $@
	cd .. && tar czf ${snapshot_dir}.tar.gz --exclude="debian" ${snapshot_dir}
	cd ../${snapshot_dir} && find . > 00index.txt
	cd .. && ln -fs ${snapshot_dir}.tar.gz ${package}-snapshot_${snapshot_version}.orig.tar.gz 
	cd .. && ln -fs ${snapshot_dir} ${package}-snapshot
	echo "# cd ../${snapshot_dir}"


fix-release: overide
	-cp TuxGuitar/scripts/tuxguitar.tg  ./
	-cp TuxGuitar/scripts/tuxguitar.xpm  ./
	-cp TuxGuitar/scripts/tuxguitar.gp3  ./

clean/snapshot:
	rm -f *.tg *.gp3
#}

refresh:
	 ls | grep TuxGuitar- | xargs echo modules?= > tmp.mk

DEBPACKAGE_VERSION?=$(shell dpkg-parsechangelog  | sed -n 's/Version: \(.*\)/\1/p')
DEBPACKAGE?=$(shell dpkg-parsechangelog | sed -n 's/Source: \(.*\)/\1/p')


sync:
	${MAKE} deb # || make snapshot && cd ../${snapshot_dir} && ${MAKE} deb
	diffstat  tmp.diff


chk:
	ls ../${package}*${snapshot_version}*.changes || ls ../${package}*.changes
	lintian -i ../tuxguitar-snapshot*.changes



tarball_name=TuxGuitar-full-1.0-rc1-src

#	cd .. && ln -fs ${CURDIR} ${tarball_name} 

#to-upstream:
#	cp ./debian/${package}.sh TuxGuitar/scripts/
#	cp ./debian/${package}.sgml TuxGuitar/scripts/
#	cp ./debian/${package}.desktop TuxGuitar/scripts/


release: tarball
	mkdir -p ../tmp/${tarball_name} && cd ../tmp/${tarball_name} \
	 && tar xf ${CURDIR}/../${tarball_name}.tar.gz \
	 && cd ${tarball_name} && make build
	echo "../${tarball_name}.tar.gz"

cvsrelease:
	${MAKE} fetch
	${MAKE} -C ../${tarball_name} clean tarball
	ls -l ../${tarball_name}.tar.gz

fetch:
	mkdir -p ../${tarball_name}
	cd  ../${tarball_name} && \
	 cvs -d${scmroot} login  && \
	 cvs -d${scmroot} co .
	echo "cd ../${tarball_name}"

#test-help: ${CURDIR}
#	echo "$<"
#	echo "${<F}"
#	echo "${<N}"
#	echo "${<D}"

sf:
	lynx -dump "http://sourceforge.net/project/showfiles.php?group_id=155855&package_id=173684&release_id=580322" | grep filename | sed -e 's|.* http:.*filename=\(.*\)|http://heanet.dl.sourceforge.net/sourceforge/tuxguitar/\1|g' | xargs -n 1 wget -nc
	unp *-src.tar.gz
	rm *.tar.gz *.bin *.exe *.zip 
	for t in *-1.0-rc1-src ; do  echo mv $t ${t/-1.0-rc1-src/}; done

install_plugins: ${modules_plugins}
	install $</$<.jar ${INSTALL_SHARE_DIR}

#snapshot-test: ${CURDIR}
#	echo ${<:%-1.0.0=%}
#	echo ${<:%-1.0.0=%-snapshot}
#	echo ${<:%-%=%-snapshot}
#	echo ${<:%-1.0.0=%-snapshot}

install-libs: ${modules_jni}
	echo ${^:TuxGuitar-%=tuxguitar-%} 
	for t in ${^} ; do  \
	 echo install -s  $$t/$${t/TuxGuitar/tuxguitar} \
	 ; done
#	 echo install -s $$t/lib$(shell echo $${t} | tr [A-Z] [a-z])-jni.so  ${INSTALL_LIB_DIR}/ ; 
# TODO:
#	for t in ${modules_plugins} ; do cd "${CURDIR}/$$t" && \
#	 ant -v -d ${ANT_FLAGS} install ; \
#	done

tuxguitar-install:
	-cp -rfa ./TuxGuitar/share/files ${INSTALL_SHARE_DIR} # TODO
	-cp -rfa ./TuxGuitar/share/services ${INSTALL_SHARE_DIR} # TODO
	-cp -rfa ./TuxGuitar/share/themes ${INSTALL_SHARE_DIR} # TODO

CURDIR?=$(shell pwd)

#tarball:$(shell pwd)
#	find . -type l -exec file {} \; | sed -e "s|\(.*\): symbolic link to \`\(.*\)'|rm \1; cp -fa \2 \1|g"
#	find . -iname "*.jar"
#	rm ./README; cp -fa TuxGuitar/doc/README ./README
#	rm ./ChangeLog; cp -fa TuxGuitar/doc/CHANGES ./ChangeLog
#	rm ./COPYING; cp -fa TuxGuitar/doc/LICENSE ./COPYING
#	rm ./AUTHORS; cp -fa TuxGuitar/doc/AUTHORS ./AUTHORS
#	cd .. && tar cvfz  ${<F}.tar.gz \
##	 --exclude="debian" \
#	 --exclude "CVS" --exclude "CVSROOT" --exclude ".cvsignore" \
#	 --exclude="tmp.*" --exclude "build-stamp" \
#	 --exclude="changed*.diff"	 \
#	 ${<F}/*
#	ls -l ../${<F}.tar.gz
#	cd .. && ln -fs ${tarball_name}.tar.gz ${package}_${pkg_ver}.orig.tar.gz

debsrc: clean/snapshot update
	debuild && sudo debi && dpkg -L ${package}
	debuild -S | grep "_source.changes"

remove:
	-sudo aptitude -y remove ${package} ${package}-snapshot

install: remove
	debi || sudo debi


debsrc:  update remove
	dch -i # -a "WIP:"
	-fakeroot ./debian/rules clean
	-make clean 
	debuild -sa -S
	debrelease -S --dput ppa


deb: update remove
	dch -i # -a "WIP:"
	-fakeroot ./debian/rules clean
	-make clean 
	debuild -sa -S
	debuild -sa 
	sudo debi
	make clean
	debuild -sa -S
	debrelease -S --dput ppa
#	dput ppa  ../${DEBPACKAGE}*${DEBPACKAGE_VERSION}*source.changes

merge:
	-diff Makefile ../tuxguitar-snapshot-*/Makefile 
	-cp Makefile ../tuxguitar-snapshot-*/Makefile
	-diff debian/tuxguitar.sh ../tuxguitar-snapshot-*/TuxGuitar/scripts/tuxguitar.sh 
	-cp debian/tuxguitar.sh ../tuxguitar-snapshot-*/TuxGuitar/scripts/tuxguitar.sh 
	-cp /rzr.online.fr/docs/contribs/files/tuxguitar.tg ../tuxguitar-snapshot-*/TuxGuitar/scripts/
#	-rm ../tuxguitar-snapshot-*/TuxGuitar/scripts/tuxguitar.tg
	-cp tuxguitar.desktop  ../tuxguitar-snapshot-*/TuxGuitar/scripts/
	-cp tuxguitar.sgml  ../tuxguitar-snapshot-*/TuxGuitar/scripts/


apt:
	sudo aptitude install build-essential docbook-utils gcj libasound2-dev

update:
#	rm -f TuxGuitar/dist/config* TuxGuitar/build.*
#	-ssh-add
	-svn $@ .
	-svn diff > tmp.diff
	diffstat  tmp.diff

snapshot-clean:
	-rm -rf TuxGuitar/build* AUTHORS COPYING ChangeLog LICENSE README

commit: snapshot-clean clean distclean update help
	${HOME}/bin/vc.sh changed
	less tmp.diff
	svn commit

#~rzr:} various debian hack to be removed

#all: clean fix jar jni
help:
	@echo "${scmroot}"
	@echo "TODO: MOZILLA_FIVE_HOME, libc ver, plugins, jsa"
	echo "FIX: (LP: #176979) ; oef dist"
	@echo "TODO: timidity or fluidsynth installed ?"
	echo "/rzr.online.fr/docs/contribs/files/${PACKAGE}.mk"
	echo "org.herac.tuxguitar.gui.help.doc.DocException: org.eclipse.swt.SWTError: No more handles [Unknown Mozilla path (MOZILLA_FIVE_HOME not set)]"
	echo "<man-di> just In case someone wants to know: I starting commiting my eclipse 3.3 work"
	@echo "# $@"


r:
	sh /mnt/hda5/home/rzr/home/src/debian/tuxguitar/tuxguitar-0.9.99~1.0rc1/debian/tuxguitar.sh /rzr.online.fr/docs/contribs/files/tuxguitar.tg

deb-fix: TuxGuitar
	cp -a $</xml/build* $</
	cp -a $</doc/LICENSE COPYING
	cp -a $</doc/CHANGES ChangeLog
	cp -a $</doc/README ./
	cp -a $</doc/AUTHORS ./
	find . -iname "*.desktop"
	find . -iname "*.xpm"
	find . -iname "*.sgml"

#{ TODO:
clean-snapshot:
	rm -rf TuxGuitar/build* AUTHORS COPYING ChangeLog LICENSE README
#}
snapshot-run: ./TuxGuitar


snapshot-install:
	-cd debian &&  for t in  tuxguitar*.* ; do ln -fs $$t $${t/tuxguitar/tuxguitar-snapshot} ; done ;
	cd debian && ln -fs tuxguitar-alsa.install tuxguitar-snapshot-alsa.install
	cd debian && ln -fs tuxguitar-jsa.install tuxguitar-snapshot-jsa.install
	cd debian && ln -fs tuxguitar-oss.install tuxguitar-snapshot-oss.install
	cd debian && ln -fs tuxguitar.install tuxguitar-snapshot.install

	cd debian && ln -fs tuxguitar-snapshot-alsa tuxguitar-alsa
	cd debian && ln -fs tuxguitar-snapshot-oss tuxguitar-oss
	cd debian && ln -fs tuxguitar-snapshot tuxguitar


snapshot-deb: snapshot snapshot-clean update deb
	-rm -f [a-z]*

get-snapshot-source: ../${snapshot_dir}

build-gcj:
	${MAKE} build ANT_FLAGS="build.compiler=gcj" 

#eof "$Id:"
