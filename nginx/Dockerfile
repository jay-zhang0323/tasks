From nginx:1.23.3

RUN mkdir /var/www

COPY www/hello-world.html /var/www

COPY etc/default.conf /etc/nginx/conf.d/
COPY etc/nginx.conf /etc/nginx/nginx.conf

RUN chown -R nginx:nginx /var/www && chmod -R 755 /var/www && \
        chown -R nginx:nginx /var/cache/nginx && \
        chown -R nginx:nginx /var/log/nginx && \
        chown -R nginx:nginx /etc/nginx/conf.d
RUN touch /var/run/nginx.pid && \
        chown -R nginx:nginx /var/run/nginx.pid

USER nginx

ENV TZ="Asia/Tokyo"

CMD ["nginx", "-g", "daemon off;"]
