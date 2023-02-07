From nginx:1.23.3

RUN mkdir /var/www

COPY www/* /var/www/

COPY etc/default.conf /etc/nginx/conf.d/
COPY etc/nginx.conf /etc/nginx/nginx.conf

RUN chown -R nginx:nginx /var/www && chmod -R 755 /var/www && \
        chown -R nginx:nginx /var/cache/nginx && \
        chown -R nginx:nginx /var/log/nginx && \
        chown -R nginx:nginx /etc/nginx/conf.d
RUN touch /var/run/nginx.pid && \
        chown -R nginx:nginx /var/run/nginx.pid

RUN curl -L https://github.com/moparisthebest/static-curl/releases/download/v7.87.0/curl-amd64 > /tmp/curl && \
         apt remove -y curl && \
         mv /tmp/curl /usr/bin/curl && \
         chmod 755 /usr/bin/curl && \
         apt autoremove -y

RUN apt update -y && \
         apt install -y make g++ && \
         curl -L  https://download.osgeo.org/libtiff/tiff-4.5.0.tar.gz > tiff-4.5.0.tar.gz && \
         tar xvf tiff-4.5.0.tar.gz && \
         cd tiff-4.5.0 && \
         ./configure && \
         make && \
         make install && \
         apt remove -y make g++ libtiff5 && \
         apt autoremove -y && \
         cd .. && rm -rf tiff-4.5.0.tar.gz tiff-4.5.0

USER nginx

EXPOSE 80

ENV TZ="Asia/Tokyo"

CMD ["nginx", "-g", "daemon off;"]
